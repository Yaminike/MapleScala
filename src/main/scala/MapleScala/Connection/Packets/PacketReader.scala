package MapleScala.Connection.Packets

import java.nio.ByteBuffer

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
class PacketReader(final val buffer: ByteBuffer) {
  def readByte = buffer.get

  def readShort = buffer.getShort

  def readInt = buffer.getInt

  def readLong = buffer.getLong

  def readBytes(amount: Int) = {
    val result: Array[Byte] = new Array(amount)
    buffer.get(result)
    result
  }

  def readString(amount: Int) = new String(readBytes(amount))

  def readMapleString = new String(readBytes(readShort))

  def skip(amount: Int) = buffer.position(amount + buffer.position())

  def reset = buffer.clear()

  def array = buffer.array()
}
