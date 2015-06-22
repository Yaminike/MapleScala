package PKGNX.Util

import java.nio.charset.{Charset, CharsetDecoder}
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
class BufferedReader(val buffer: ByteBuffer) {
  buffer.order(ByteOrder.LITTLE_ENDIAN)

  var markPosition: Int = 0

  def skip(amount: Int) = buffer.position(buffer.position + amount)

  def seek(position: Long) = buffer.position(position.toInt)

  def mark(): Unit = {
    markPosition = buffer.position()
  }

  def reset(): Unit = seek(markPosition)

  def getBytes(length: Int): Array[Byte] = {
    val data = new Array[Byte](length)
    buffer.get(data)
    data
  }

  def getUShort: Int = buffer.getShort & 0x0000FFFF

  def getInt: Int = buffer.getInt

  def getUInt: Long = buffer.getInt & 0xFFFFFFFFL

  def getLong: Long = buffer.getLong

  def getDouble: Double = buffer.getDouble

  def getString: String = getString(getUShort)

  def getString(length: Int): String = {
    val data = new Array[Byte](length)
    buffer.get(data)
    BufferedReader.utfDecoder.get().decode(ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)).toString
  }
}

object BufferedReader {
  final val utfDecoder: ThreadLocal[CharsetDecoder] = new ThreadLocal[CharsetDecoder] {
    override protected def initialValue(): CharsetDecoder = Charset.forName("UTF-8").newDecoder()
  }
}
