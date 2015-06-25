package MapleScala.Connection.Packets

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
object RecvOpcode {
  final val LoginPassword: Short = 0x0001
  final val ServerlistReRequest: Short = 0x0004
  final val CharacterlistRequest: Short = 0x0005
  final val ServerstatusRequest: Short = 0x0006
  final val AfterLogin: Short = 0x0009
  final val RegisterPin: Short = 0x000A
  final val ServerlistRequest: Short = 0x000B
  final val PlayerDisconnect: Short = 0x000C

  final val CheckCharacterName: Short = 0x0015
  final val CreateCharacter: Short = 0x0016
  final val Pong: Short = 0x0018
  final val ClientStartError: Short = 0x0019

  final val MapLogin: Short = 0x0023
}

object SendOpcode {
  final val Loginstatus: Short = 0x0000
  final val Serverstatus: Short = 0x0003
  final val CheckPin: Short = 0x0006
  final val UpdatePin: Short = 0x0007
  final val Serverlist: Short = 0x000A
  final val Characterlist: Short = 0x000B
  final val CharacterNameResponse: Short = 0x000D
  final val AddNewCharacter: Short = 0x000E

  final val Ping: Short = 0x0011
  final val DefaultWorld: Short = 0x001A
}
