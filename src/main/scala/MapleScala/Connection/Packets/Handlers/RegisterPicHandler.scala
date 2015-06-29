package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import akka.io.Tcp.Abort
import io.github.nremond.SecureHash

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
  def handle(packet: PacketReader, client: Client): Unit = {
    for (user <- client.loginstate.user) {
      if (user.pic.nonEmpty) {
        client.connection ! Abort // Trying to register a pin while already having one registered
        return
      }

      packet.skip(1)

      val charId = packet.getInt

      packet.skip(packet.getShort) // Mac address?
      packet.skip(packet.getShort) // Unk

      user.pic = Some(SecureHash.createHash(packet.getString))
      user.save()

      // Todo: Migrate
    }
  }
}
