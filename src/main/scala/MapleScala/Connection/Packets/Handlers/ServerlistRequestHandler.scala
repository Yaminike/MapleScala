package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.{MapleString, PacketReader, PacketWriter, SendOpcode}
import MapleScala.Main
import com.typesafe.config.Config

import scala.collection.JavaConversions._

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
  final val worlds = Main.conf.getConfigList("server.worlds")
  final val defaultWorld = Main.conf.getInt("server.defaultWorld")

  def handle(packet: PacketReader, client: Client): Unit = {
    for (world <- worlds)
      createServerlist(client, world)
    endServerlist(client)
    selectDefaultWorld(client)
  }

  private def createServerlist(client: Client, world: Config): Unit = {
    val index: Byte = world.getInt("id").toByte
    val channel: Byte = world.getInt("channels").toByte
    val name: String = world.getString("name")
    val pw = new PacketWriter()
      .write(SendOpcode.Serverlist)
      .write(index)
      .write(new MapleString(name))
      .write(0.toByte) // TODO: Flag
      .write(new MapleString(world.getString("event"))) // TODO: Eventmessage
      .write(100.toByte) // Rate modifier?
      .write(0.toByte) // Event exp * 2.6 ?
      .write(100.toByte) // Rate modifier?
      .write(0.toByte) // Drop rate * 2.6 ?
      .write(0.toByte)
      .write(channel) // Channel count

    for (i <- 0 until channel) {
      pw.write(new MapleString(s"$name-${i + 1}"))
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

  private def selectDefaultWorld(client: Client): Unit = {
    val pw = new PacketWriter()
      .write(SendOpcode.DefaultWorld)
      .write(defaultWorld)

    client.self ! pw
  }
}
