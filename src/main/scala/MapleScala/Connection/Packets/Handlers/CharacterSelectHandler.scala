package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{SendOpcode, PacketWriter, PacketReader}
import MapleScala.Main

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
object CharacterSelectHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    if (Main.picEnabled) {
      // Oh you silly hacker
      client.self ! invalidPic
      return
    }

    for (user <- client.loginstate.user) {
      val characterId = packet.getInt
      packet.skip(packet.getShort) // Mac address?
      client.loginstate.character = user.getCharacter(characterId)
      client.migrate()
    }
  }

  def invalidPic: PacketWriter =
    new PacketWriter()
      .write(SendOpcode.CheckSPWResult)
      .empty(1)
}
