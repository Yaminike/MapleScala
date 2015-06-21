package MapleScala.Connection.Packets.Handlers

import MapleScala.Client.MapleCharacter
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{SendOpcode, PacketWriter, PacketReader}

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
object CharlistRequestHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    packet.skip(1)
    val world = packet.readByte
    val channel = packet.readByte

    showCharlist(client, world)
  }

  def showCharlist(client: Client, world: Byte): Unit = {
    val characters = client.user.characters.filter(_.world == world)

    val pw = new PacketWriter()
      .write(SendOpcode.Charlist)
      .write(0.toByte)
      .write(characters.length.toByte) // Amount of characters

    characters
      .map(_.asInstanceOf[MapleCharacter])
      .foreach(_.addCharEntry(pw, true))

    pw.write(2.toByte) // TODO: PIC
    pw.write(9) // TODO: Character slots

    client.self ! pw
  }
}
