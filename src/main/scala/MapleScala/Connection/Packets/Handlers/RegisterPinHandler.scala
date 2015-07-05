package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthResponse, AuthStatus}
import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{PacketReader, PacketWriter, SendOpcode}
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
object RegisterPinHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    for (user <- client.loginstate.user) {
      (client.auth ? new AuthRequest.GetStatus(user.id)).onComplete({
        case Success(response: AuthResponse.GetStatus) =>
          if (packet.getByte != 0) {
            if (response.holder.status.contains(AuthStatus.LoggedIn) &&
              (response.holder.status.contains(AuthStatus.PinAccepted) || user.pin.isEmpty)) {
              val pin = packet.getString
              if (pin.forall(_.isDigit)) {
                user.pin = Some(pin.toInt)
                user.save()
              }
            }
          }
          updatePin(client)
        case _ => updatePin(client)
      })(client.context.dispatcher)
    }
  }

  def updatePin(client: Client): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.UpdatePin)
      .write(0.toByte)

    client.self ! pw

    client.logout()
  }
}
