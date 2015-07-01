package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthResponse}
import MapleScala.Client.MapleCharacter
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import MapleScala.Data.Character
import akka.io.Tcp.Abort
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.Success

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
object PlayerLoggedinHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    val key = packet.getInt
    (client.auth ? new AuthRequest.GetMigration(key)).onComplete({
      case Success(response: AuthResponse.GetMigration) =>
        Character.getById(response.data.charId) match {
          case Some(player) => createResponse(player, response.data.channel)
          case None => client.connection ! Abort
        }
      case _ => client.connection ! Abort
    })(client.context.dispatcher)
  }

  def createResponse(player: MapleCharacter, channel: Byte): Unit = {
    println("")
  }
}
