package MapleScala.Connection.Packets

import java.nio.ByteOrder

import akka.util.{ByteString, ByteStringBuilder}

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
class PacketWriter {
  private final val byteOrder: ByteOrder = ByteOrder.LITTLE_ENDIAN
  private final val buffer = new ByteStringBuilder()

  def write(v: Byte): Unit = buffer.putByte(v)

  def write(v: Short): Unit = buffer.putShort(v)(byteOrder)

  def write(v: Int): Unit = buffer.putInt(v)(byteOrder)

  def write(v: Long): Unit = buffer.putLong(v)(byteOrder)

  def write(v: Array[Byte]): Unit = buffer.putBytes(v)

  def write(i: MapleString): Unit = {
    write(i.value.length.toShort)
    write(i.value)
  }

  def write(v: String): Unit = buffer.putBytes(v.getBytes)

  def result: ByteString = buffer.result()
}
