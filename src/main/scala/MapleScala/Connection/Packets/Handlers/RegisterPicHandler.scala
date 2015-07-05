package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthStatus}
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import akka.io.Tcp.Abort
import akka.pattern.ask
import akka.util.Timeout
import io.github.nremond.SecureHash

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
object RegisterPicHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    packet.skip(1)
    val characterId = packet.getInt

    packet.skip(packet.getShort) // Mac address?
    packet.skip(packet.getShort) // Unk

    for {
      user <- client.loginstate.user
      character <- user.getCharacter(characterId)
    } {
      if (user.pic.nonEmpty) {
        client.connection ! Abort // Trying to register a pin while already having one registered
        return
      }

      user.pic = Some(SecureHash.createHash(packet.getString))
      user.save()

      (client.auth ? new AuthRequest.SetStatus(user.id, AuthStatus.PicAccepted)).onComplete({
        case Success(result) => client.migrate()
        case Failure(failure) => client.connection ! Abort
      })(client.context.dispatcher)

      return
    }

    client.connection ! Abort // Either no user, or no character found
  }
}
