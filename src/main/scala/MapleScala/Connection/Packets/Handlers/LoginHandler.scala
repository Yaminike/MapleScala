package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets._

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
object LoginHandler {
  def handle(packet: PacketReader, client: Client): Unit = {
    val login: String = packet.readMapleString
    val password: String = packet.readMapleString

    val pw = new PacketWriter()
      .write(SendOpcode.LOGIN_STATUS)
      .write(0)
      .write(0.toShort)
      .write(0) // Account Id
      .write(0.toByte) // Gender
      .write(false) // IsGM
      .write(0x80) // GMLevel? o-O
      .write(new MapleString("Herp")) // Account Name
      .write(0.toByte)
      .write(false) // IsQuietBanned
      .write(0L) // QuestBannedTime
      .write(0L) // CreationTime?
      .write(0)
      .write(2.toShort) // PIN mode

    client.self ! pw
  }
}
