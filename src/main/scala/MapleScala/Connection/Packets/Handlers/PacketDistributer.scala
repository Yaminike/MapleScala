package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import MapleScala.Connection.Packets.RecvOpcode._

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
    header match {
      case LOGIN_PASSWORD => LoginHandler.handle(packet, client)
      case CLIENT_START_ERROR =>
      case MAP_LOGIN => client.setActive()

      case FORCE_DISCONNECT => println("WARNING: Force disconnected a client")
      case other => println(f"Handler not found for 0x$other%04X")
    }
  }
}
