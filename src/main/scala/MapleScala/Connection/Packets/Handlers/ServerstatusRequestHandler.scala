package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{PacketReader, PacketWriter, SendOpcode}

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
object ServerstatusRequestHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    val world = packet.readShort // TODO: get the actual status
    sendServerStatus(client, 0)
  }

  private def sendServerStatus(client: Client, status: Short): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.Serverstatus)
      .write(status)

    client.self ! pw
  }
}
