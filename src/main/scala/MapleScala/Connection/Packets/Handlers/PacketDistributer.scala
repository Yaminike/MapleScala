package MapleScala.Connection.Packets.Handlers

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import MapleScala.Connection.Packets.RecvOpcode._
import akka.io.Tcp.Abort

import scala.collection.immutable

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
  // These packets do not require the user to be logged in
  final lazy val defaultHandlers: immutable.HashMap[Short, PacketHandler] = immutable.HashMap(
    LoginPassword -> LoginHandler,
    ClientStartError -> EmptyHandler,
    MapLogin -> MapLoginHandler,
    Pong -> EmptyHandler
  )

  // These packets only occur when the user is logged in
  final lazy val handlers: immutable.HashMap[Short, PacketHandler] = immutable.HashMap(
    ServerlistReRequest -> ServerlistRequestHandler,
    CharacterlistRequest -> CharacterlistRequestHandler,
    ServerstatusRequest -> ServerstatusRequestHandler,
    AfterLogin -> AfterLoginHandler,
    RegisterPin -> RegisterPinHandler,
    ServerlistRequest -> ServerlistRequestHandler,
    PlayerDisconnect -> EmptyHandler,
    CheckCharacterName -> CheckCharacterNameHandler,
    CreateCharacter -> CreateCharacterHandler
  )

  def distribute(packet: PacketReader, client: Client): Unit = {
    val header: Short = packet.getShort

    if (handlers.contains(header)) {
      if (client.loginstate.user.isEmpty)
        client.connection ! Abort
      else
        handlers(header).handle(packet, client)
    } else if (defaultHandlers.contains(header)) {
      defaultHandlers(header).handle(packet, client)
    } else {
      println(f"Handler not found for 0x$header%04X")
    }
  }
}
