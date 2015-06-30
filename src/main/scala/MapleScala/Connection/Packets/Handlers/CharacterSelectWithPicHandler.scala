package MapleScala.Connection.Packets.Handlers

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
object CharacterSelectWithPicHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    val pic = packet.getString
    val charId = packet.getInt
    packet.skip(packet.getShort) // Mac address?

    for {
      user <- client.loginstate.user
      character <- user.getCharacter(charId)
    } {
      if (user.validatePIC(pic))
      {
        // Todo: Migrate
        return
      }
    }

    client.self ! invalidPic // Either invalid pic, no user found or no character found
  }

  def invalidPic: PacketWriter =
    new PacketWriter()
      .write(SendOpcode.CheckSPWResult)
      .empty(1)
}
