package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthResponse}
import MapleScala.Connection.Client
import MapleScala.Connection.Packets._
import MapleScala.Data.User
import MapleScala.Util.Extensions._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

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
object LoginHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    val username: String = packet.getString
    val password: String = packet.getString

    (client.auth ? new AuthRequest.Login(username, password)).onComplete({
      case Success(response: AuthResponse.Login) =>
        if (response.result == 0) {
          client.loginstate.user = response.user
          for (user <- response.user) {
            validLogin(user, client)
          }
        } else {
          failedLogin(client, response.result)
        }
      case Failure(failure) =>
        failedLogin(client, 6)
    })(client.context.dispatcher)
  }

  private def validLogin(user: User, client: Client): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.Loginstatus)
      .write(0)
      .write(0.toShort)
      .write(user.id) // Account Id
      .write(0.toByte) // Gender
      .write(user.isGM) // IsGM
      .write(0x80) // GMLevel? o-O
      .write(user.name.toMapleString) // Account Name
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
      .write(SendOpcode.Loginstatus)
      .write(reason)
      .write(0.toByte)
      .write(0)

    client.self ! pw
  }
}
