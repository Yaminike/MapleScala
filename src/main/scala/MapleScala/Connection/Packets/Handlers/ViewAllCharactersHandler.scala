package MapleScala.Connection.Packets.Handlers

import MapleScala.Client.MapleCharacter
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
object ViewAllCharactersHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    for (user <- client.loginstate.user) {
      val characters = user.getCharacters
      client.self ! sendCount(characters.length)

      for (world <- characters.map(_.world).distinct) {
        client.self ! showCharactersByWorld(world, characters.filter(_.world == world))
      }
    }
  }

  def sendCount(count: Int): PacketWriter = {
    new PacketWriter()
      .write(SendOpcode.ViewAllCharacters)
      .write(true)
      .write(count)
      // Basically the lowest number divisible by 3 that can contain the count, so math.ceil(count / 3) * 3
      .write(count + 3 - count % 3)
  }

  def showCharactersByWorld(world: Byte, characters: List[MapleCharacter]): PacketWriter = {
    val pw = new PacketWriter()
      .write(SendOpcode.ViewAllCharacters)
      .write(false)
      .write(world)
      .write(characters.length.toByte)

    for (character <- characters) {
      character.addCharEntry(pw, viewall = true)
    }

    pw
  }
}
