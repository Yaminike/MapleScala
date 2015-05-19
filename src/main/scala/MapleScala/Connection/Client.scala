package MapleScala.Connection

import MapleScala.Connection.Packets.{MapleString, PacketWriter}
import MapleScala.Crypto.CipherHelper
import akka.actor.{Actor, ActorRef, Props}
import akka.io._
import akka.util.ByteString

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
  def create(connection: ActorRef): Props = Props(new Client(connection))
}

class Client(val connection: ActorRef) extends Actor {

  import Tcp._

  private final val cipher = new CipherHelper()
  handshake

  def receive = {
    case Received(data) => parseData(data)
    case PeerClosed => context.stop(self)
  }

  private def parseData(data: ByteString): Unit = {
    val buffer = cipher.decrypt(data.asByteBuffer)
    println(MapleScala.Helper.toHex(buffer.array()))
  }

  private def handshake = {
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
