package MapleScala.Connection.Packets.Handlers

import MapleScala.Client.{MapleCharacter, MapleJob}
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{PacketReader, PacketWriter, SendOpcode}
import MapleScala.Data
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
object CreateCharacterHandler extends PacketHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    val name: String = packet.getString
    if (!MapleCharacter.isValidName(name)) {
      client.connection ! Abort
      return
    }

    val job = packet.getInt
    val face = packet.getInt
    val hair = packet.getInt + packet.getInt
    val skin = packet.getInt

    // Check if skin is valid
    if (skin < 0 || skin > 3) {
      client.connection ! Abort
      return
    }

    val character = MapleCharacter.getDefault
    character.userId = client.loginstate.user.get.id
    character.world = client.loginstate.world
    character.name = name
    character.face = face
    character.hair = hair
    character.skinColor = skin.toByte

    val top = packet.getInt
    val bottom = packet.getInt
    val shoes = packet.getInt
    val weapon = packet.getInt

    character.gender = packet.getBool

    // Check if equips are valid
    if (
      !isValidEquip(top) ||
        !isValidEquip(bottom) ||
        !isValidEquip(shoes) ||
        !isValidEquip(weapon)
    ) {
      client.connection ! Abort
      return
    }

    // Match job, default to Adventurer
    job match {
      case 0 =>
        // Knights of Cygnus
        character.job = MapleJob.Noblesse
      case 2 =>
        // Aran
        character.job = MapleJob.Legend
      case _ =>
      // Adventurer
    }

    character.save()

    client.self ! response(character)
  }

  def response(character: MapleCharacter): PacketWriter = {
    val pw = new PacketWriter()
      .write(SendOpcode.AddNewCharacter)
      .empty(1)
    character.addCharEntry(pw, viewall = false)

    pw
  }


  def isValidEquip(id: Long): Boolean = Data.WZ.Etc.allowedEquips.contains(id)
}
