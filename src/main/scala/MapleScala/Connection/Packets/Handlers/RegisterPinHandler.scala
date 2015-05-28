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
object RegisterPinHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    val authRequest = client.server ? new AuthRequest.GetStatus(client.user.id)
    authRequest.onComplete({
      case Success(result) =>
        if (packet.readByte != 0) {
          val response = result.asInstanceOf[AuthResponse.GetStatus]
          if (response.status.contains(AuthStatus.LoggedIn) &&
            (response.status.contains(AuthStatus.PinAccepted) || client.user.pin.isEmpty)) {
            val pin = packet.readMapleString
            if (pin.forall(_.isDigit)) {
              client.user.pin = Option(pin.toInt)
              client.user.updateUser()
            }
          }
        }
        updatePin(client)
      case Failure(failure) => updatePin(client)
    })(client.context.dispatcher)
  }

  def updatePin(client: Client): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.UpdatePin)
      .write(0.toByte)

    client.self ! pw

    client.logout()
  }
}
