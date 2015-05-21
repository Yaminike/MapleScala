package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets._
import MapleScala.Data.User

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
    val user: User = User.getByName(login)
    if (user == null) {
      // TODO: User not found
      return
    }

    val password: String = packet.readMapleString
    if(!user.validatePassword(password)) {
      // TODO: Invalid password
      return
    }
  }

  def validLogin(user: User, client: Client): Unit ={
    val pw = new PacketWriter()
      .write(SendOpcode.LOGIN_STATUS)
      .write(0)
      .write(0.toShort)
      .write(user.id) // Account Id
      .write(0.toByte) // Gender
      .write(user.isGM) // IsGM
      .write(0x80) // GMLevel? o-O
      .write(new MapleString(user.name)) // Account Name
      .write(0.toByte)
      .write(false) // IsQuietBanned
      .write(0L) // QuietBannedTime
      .write(0L) // CreationTime?
      .write(0)
      .write(2.toShort) // PIN mode

    client.self ! pw
  }
}
