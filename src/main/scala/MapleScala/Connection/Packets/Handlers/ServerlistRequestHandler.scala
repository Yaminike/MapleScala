package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{MapleString, SendOpcode, PacketWriter, PacketReader}
import MapleScala.Main

import collection.JavaConversions._

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
object ServerlistRequestHandler extends PacketHandler {
  final val worlds = Main.conf.getStringList("server.worlds.names").toList
  final val channels = Main.conf.getIntList("server.worlds.channels").toList

  def handle(packet: PacketReader, client: Client): Unit = {
    for (world <- worlds)
      createServerlist(client, world)
    endServerlist(client)
  }

  private def createServerlist(client: Client, world: String): Unit = {
    val index: Byte = worlds.indexOf(world).toByte
    val channel: Byte = channels.get(index).toByte
    val pw = new PacketWriter()
      .write(SendOpcode.Serverlist)
      .write(index)
      .write(new MapleString(world))
      .write(0.toByte) // TODO: Flag
      .write(new MapleString("")) // TODO: Eventmessage
      .write(100.toByte) // Rate modifier?
      .write(0.toByte) // Event exp * 2.6 ?
      .write(100.toByte) // Rate modifier?
      .write(0.toByte) // Drop rate * 2.6 ?
      .write(0.toByte)
      .write(channel) // Channel count

    for (i <- 0 until channel) {
      pw.write(new MapleString(s"$world-${i + 1}"))
        .write(0) // TODO: Channel load
        .write(true)
        .write(i.toShort)
    }
    
    pw.empty(2)

    client.self ! pw
  }

  private def endServerlist(client: Client): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.Serverlist)
      .write(0xFF.toByte)

    client.self ! pw
  }
}
