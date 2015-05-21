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
  final val FORCE_DISCONNECT: Short = 0x0000
  final val LOGIN_PASSWORD: Short = 0x0001
  final val CLIENT_START_ERROR: Short = 0x0019
  final val MAP_LOGIN: Short = 0x0023
}

object SendOpcode {
  final val LOGIN_STATUS: Short = 0x0000
  final val PING: Short = 0x0011
}
