package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import MapleScala.Connection.Packets.RecvOpcode._
import akka.io.Tcp.Abort

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
object PacketDistributer {
  def distribute(packet: PacketReader, client: Client): Unit = {
    val header: Short = packet.readShort

    if (client.user == null) {
      header match {
        // All these packets only occur when the user is logged in
        case AfterLogin |
             RegisterPin =>
          client.connection ! Abort

        // These packets can occur when the user is not logged in
        case LoginPassword => LoginHandler.handle(packet, client)
        case ClientStartError =>
        case MapLogin => client.setActive()

        case ForceDisconnect => println("WARNING: Force disconnected a client")
        case other => println(f"Handler not found for 0x$other%04X")
      }
    } else {
      header match {
        case LoginPassword => LoginHandler.handle(packet, client)
        case AfterLogin => AfterLoginHandler.handle(packet, client)
        case RegisterPin => RegisterPinHandler.handle(packet, client)
        case ClientStartError =>
        case MapLogin => client.setActive()

        case ForceDisconnect => println("WARNING: Force disconnected a client")
        case other => println(f"Handler not found for 0x$other%04X")
      }
    }
  }
}
