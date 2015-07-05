package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthStatus}
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{PacketReader, PacketWriter, SendOpcode}
import akka.io.Tcp.Abort
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
object CharacterSelectWithPicHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    val pic = packet.getString
    val characterId = packet.getInt
    packet.skip(packet.getShort) // Mac address?

    for (user <- client.loginstate.user) {
      if (user.validatePIC(pic)) {
        client.loginstate.character = user.getCharacter(characterId)
        (client.auth ? new AuthRequest.SetStatus(user.id, AuthStatus.PicAccepted)).onComplete({
          case Success(result) => client.migrate()
          case Failure(failure) => client.connection ! Abort
        })(client.context.dispatcher)
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
