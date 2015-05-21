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
      failedLogin(client, 5)
      return
    }

    val password: String = packet.readMapleString
    if (!user.validatePassword(password)) {
      failedLogin(client, 4)
      return
    }

    validLogin(user, client)
  }

  private def validLogin(user: User, client: Client): Unit = {
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

  /*
  Possible reasons:
    3) ID deleted or blocked
    4) Incorrect password
    5) Not a registered id
    6) System error
    7) Already logged in
    8,9) System error
    10) Cannot process so many connections
    11) Only users older than 20 can use this channel
    13) Unable to log on as master at this ip
    16) Please verify your account through email...
    17) Wrong gateway or personal info
    21) Please verify your account through email...
    23) License agreement
    25) Maple Europe region notice
  */
  private def failedLogin(client: Client, reason: Byte): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.LOGIN_STATUS)
      .write(reason)
      .write(0.toByte)
      .write(0)

    client.self ! pw
  }
}
