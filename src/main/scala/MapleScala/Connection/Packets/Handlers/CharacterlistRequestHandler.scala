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
object CharacterlistRequestHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    packet.skip(1)
    val world = packet.getByte
    val channel = packet.getByte

    client.loginstate.world = world
    client.loginstate.channel = channel

    showCharacterlist(client, world)
  }

  def showCharacterlist(client: Client, world: Byte): Unit = {
    for (user <- client.loginstate.user) {
      val characters = user.getCharacters.filter(_.world == world)

      val pw = new PacketWriter()
        .write(SendOpcode.Characterlist)
        .write(0.toByte)
        .write(characters.length.toByte) // Amount of characters

      characters.foreach(_.addCharEntry(pw, viewall = true))

      pw.write(2.toByte) // TODO: PIC
      pw.write(9) // TODO: Character slots

      client.self ! pw
    }
  }
}
