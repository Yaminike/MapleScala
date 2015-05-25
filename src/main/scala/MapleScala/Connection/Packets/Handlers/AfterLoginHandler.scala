package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{SendOpcode, PacketWriter, PacketReader}
import akka.io.Tcp.Abort

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
object AfterLoginHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    if (client.user == null) {
      // Not logged in
      client.connection ! Abort
      return
    }

    val step = packet.readByte
    var state: Byte = -1
    if (packet.available > 0)
      state = packet.readByte

    step match {
      case 0 =>
        if (state == -1) {
          // Cancel
          client.user == null
        }
      case 1 =>
        if (state == 0) {
          val pin = packet.readMapleString
          println(s"test1 $pin")
        } else if (state == 1) {
          // TODO: Register
          pinOperation(client, Reasons.REQUEST)
        }
      case 2 =>
        if (state == 0) {
          val pin = packet.readMapleString
          println(s"test2 $pin")
        }
    }
  }

  private def pinOperation(client: Client, reason: Byte): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.CHECK_PINCODE)
      .write(reason)

    client.self ! pw
  }

  private object Reasons {
    final val REQUEST: Byte = 0x04
    final val REQUEST_AFTER_FAILURE: Byte = 0x02
    final val REGISTER: Byte = 0x01
    final val ACCEPT: Byte = 0x00
  }
}
