package MapleScala.Util

import MapleScala.Connection.Packets.MapleString

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
object Extensions {

  implicit class ExtendedLong(val value: Long) extends AnyVal {
    def toMapleTime: Long = value match {
      case -1 => 150842304000000000L // DEFAULT_TIME
      case -2 => 94354848000000000L // ZERO_TIME
      case -3 => 150841440000000000L // PERMANENT
      case _ => value * 10000 + 116444592000000000L // FT_UT_OFFSET
    }
  }

  implicit class ExtendedString(val value: String) extends AnyVal {
    def toMapleString: MapleString = new MapleString(value)
  }

}
