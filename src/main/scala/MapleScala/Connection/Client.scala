package MapleScala.Connection

import MapleScala.Connection.Packets.Handlers.PacketDistributer
import MapleScala.Connection.Packets._
import MapleScala.Crypto.CipherHelper
import MapleScala.Data.User
import akka.actor.{Actor, ActorRef, Props}
import akka.io._

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
  def create(connection: ActorRef, server: ActorRef): Props = Props(new Client(connection, server))
}

class Client(val connection: ActorRef, val server: ActorRef) extends Actor {

  class Send(val data: PacketWriter)

  import Tcp._

  private final val cipher = new CipherHelper(this)
  var user: User = null

  handshake()

  def receive = {
    case pw: PacketWriter => connection ! Write(cipher.encrypt(pw.result))
    case Received(data) => PacketDistributer.distribute(cipher.decrypt(data.asByteBuffer), this)
    case _: ConnectionClosed => disconnect()
  }

  def setActive() = {
    // TODO: Ping/Pong
    val pw = new PacketWriter()
      .write(SendOpcode.PING)
    self ! pw
  }

  private def disconnect() = {
    context.stop(self)
    println("Disconnected") // TODO: Remove
  }

  private def handshake() = {
    val pw = new PacketWriter()
    pw.write(0x0E.toShort)
    pw.write(83.toShort) // Version
    pw.write(new MapleString("1")) // SubVersion
    pw.write(cipher.RIV)
    pw.write(cipher.SIV)
    pw.write(8.toByte)
    connection ! Write(pw.result)
  }
}
