package MapleScala.Util

import java.nio.{ByteBuffer, ByteOrder}

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
class LittleEndianReader(val buffer: ByteBuffer) {
  buffer.order(ByteOrder.LITTLE_ENDIAN)

  var markPosition: Int = 0

  def getBool = buffer.get == 1

  def getByte = buffer.get

  def getBytes(amount: Int) = {
    val result: Array[Byte] = new Array(amount)
    buffer.get(result)
    result
  }

  def getShort = buffer.getShort

  def getUShort: Int = buffer.getShort & 0x0000FFFF

  def getInt = buffer.getInt

  def getUInt: Long = buffer.getInt & 0xFFFFFFFFL

  def getLong = buffer.getLong

  def getDouble: Double = buffer.getDouble

  def getString: String = getString(getUShort)

  def getString(amount: Int) = new String(getBytes(amount))

  def skip(amount: Int) = buffer.position(amount + buffer.position())

  def clear(): Unit = buffer.clear()

  def mark(): Unit = markPosition = buffer.position()

  def reset(): Unit = seek(markPosition)

  def seek(position: Long) = buffer.position(position.toInt)

  def toArray: Array[Byte] = buffer.array()

  def available = buffer.limit() - buffer.position()
}
