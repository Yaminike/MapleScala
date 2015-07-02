package MapleScala.Connection

import MapleScala.Authorization.{AuthRequest, AuthResponse}
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
  val loginstate = new Loginstatus

  def receive = {
    case pw: PacketWriter => connection ! Write(cipher.encrypt(pw.result))
    case Received(data) => PacketDistributer.distribute(cipher.decrypt(data.asByteBuffer), this)
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
      auth ! new AuthRequest.Logout(user.id)
      loginstate.user = None
    }
  }

  def migrate(userId: Int, charId: Int): Unit = {
    (auth ? new AuthRequest.Migrate(userId, charId, loginstate.channel)).onComplete({
      case Success(result: AuthResponse.Migrate) =>
        self ! new PacketWriter()
          .write(SendOpcode.ServerIp)
          .empty(2)
          .write(MapleScala.Main.serverIp.split('.').map(_.toByte))
          .write(MapleScala.Main.worldMap.getOrElse(loginstate.world, 0).toShort)
          .write(result.key)
          .empty(5)

      case _ => connection ! Abort
    })(context.dispatcher)
  }

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
