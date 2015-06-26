package MapleScala.Connection.Packets.Handlers

import MapleScala.Authorization.{AuthRequest, AuthStatus}
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
object AfterLoginHandler extends PacketHandler {
  implicit val timeout = Timeout(5.seconds)

  def handle(packet: PacketReader, client: Client): Unit = {
    val step = packet.getByte
    val user = client.loginstate.user
    var state: Byte = -1
    if (packet.available > 0)
      state = packet.getByte

    step match {
      case 0 =>
        if (state == -1) {
          // Cancel
          client.logout()
        }
      case 1 =>
        if (state == 0) {
          val pin = packet.getString
          if (pin.forall(_.isDigit) && user.validatePIN(pin.toInt)) {
            val authRequest = client.auth ? new AuthRequest.SetStatus(user.id, AuthStatus.PinAccepted)
            authRequest.onComplete({
              case Success(result) => pinOperation(client, Reasons.Accept)
              case Failure(failure) => pinOperation(client, Reasons.RequestAfterFailure)
            })(client.context.dispatcher)
          } else {
            pinOperation(client, Reasons.RequestAfterFailure)
          }
        } else if (state == 1) {
          if (user.pin.nonEmpty)
            pinOperation(client, Reasons.Request)
          else
            pinOperation(client, Reasons.Register)
        }
      case 2 =>
        if (state == 0) {
          val pin = packet.getString
          if (pin.forall(_.isDigit) && user.validatePIN(pin.toInt))
            pinOperation(client, Reasons.Register)
          else
            pinOperation(client, Reasons.RequestAfterFailure)
        }
    }
  }

  private def pinOperation(client: Client, reason: Byte): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.CheckPin)
      .write(reason)

    client.self ! pw
  }

  private object Reasons {
    final val Request: Byte = 0x04
    final val RequestAfterFailure: Byte = 0x02
    final val Register: Byte = 0x01
    final val Accept: Byte = 0x00
  }

}
