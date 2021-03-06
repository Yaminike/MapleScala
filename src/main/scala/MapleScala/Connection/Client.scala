package MapleScala.Connection

import MapleScala.Authorization.AuthRequest
import MapleScala.Connection.Packets.Handlers.PacketDistributer
import MapleScala.Connection.Packets._
import MapleScala.Crypto.CipherHelper
import MapleScala.Util.Extensions._
import akka.actor.{Actor, ActorRef, Props}
import akka.io._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.Success

/**
 * Copyright 2015 Yaminike
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object Client {
  def create(connection: ActorRef, auth: ActorRef): Props = Props(new Client(connection, auth))

  class Handshake

}

class Client(val connection: ActorRef, val auth: ActorRef) extends Actor {
  implicit val timeout = Timeout(5.seconds)

  import Tcp._

  private final val cipher = new CipherHelper(this)
  var loginstate = new Loginstatus

  def receive = cipher.synchronized {
    case pw: PacketWriter => connection ! Write(cipher.encrypt(pw.result))
    case Received(data) => cipher.decrypt(data.asByteBuffer).foreach(PacketDistributer.distribute(_, this))
    case _: ConnectionClosed => disconnect()
    case _: Client.Handshake => handshake()
  }

  def setActive() = {
    // TODO: Ping/Pong
    val pw = new PacketWriter()
      .write(SendOpcode.Ping)
    self ! pw
  }

  def logout(): Unit = {
    for (user <- loginstate.user) {
      if (!loginstate.isMigrating)
        auth ! new AuthRequest.Logout(user.id)
      loginstate = new Loginstatus
    }
  }

  def migrate(changeChannel: Boolean = false): Unit = {
    loginstate.isMigrating = true
    for {
      user <- loginstate.user
      character <- loginstate.character
    } {
      (auth ? new AuthRequest.CreateMigration(user.id, character.id, loginstate.channel)).onComplete({
        case Success(key: Int) =>
          val ip = MapleScala.Main.serverIp.split('.').map(_.toByte)
          val port = MapleScala.Main.worldMap.getOrElse(loginstate.world, 0) + loginstate.channel
          self ! {
            if (changeChannel)
              channelMigrate(ip, port.toShort)
            else
              loginMigrate(key, ip, port.toShort)
          }
        case _ => connection ! Abort
      })(context.dispatcher)
    }
  }

  private def channelMigrate(ip: Array[Byte], port: Short): PacketWriter = new PacketWriter()
    .write(SendOpcode.ChangeChannel)
    .write(true)
    .write(ip)
    .write(port)

  private def loginMigrate(key: Int, ip: Array[Byte], port: Short): PacketWriter = new PacketWriter()
    .write(SendOpcode.ServerIp)
    .empty(2)
    .write(ip)
    .write(port)
    .write(key)
    .empty(5)

  private def disconnect() = {
    logout()
    context.stop(self)
    println("Disconnected") // TODO: Remove
  }

  private def handshake() = {
    val pw = new PacketWriter()
      .write(0x0E.toShort)
      .write(83.toShort) // Version
      .write("1".toMapleString) // SubVersion
      .write(cipher.RIV)
      .write(cipher.SIV)
      .write(8.toByte)
    connection ! Write(pw.result)
  }
}
