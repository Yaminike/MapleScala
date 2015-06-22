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

  def write(v: Boolean): PacketWriter = {
    if (v)
      write(1.toByte)
    else
      write(0.toByte)
  }

  def write(v: Byte): PacketWriter = {
    buffer.putByte(v)
    this
  }

  def write(v: Short): PacketWriter = {
    buffer.putShort(v)(byteOrder)
    this
  }

  def write(v: Int): PacketWriter = {
    buffer.putInt(v)(byteOrder)
    this
  }

  def write(v: Long): PacketWriter = {
    buffer.putLong(v)(byteOrder)
    this
  }

  def write(v: Array[Byte]): PacketWriter = {
    buffer.putBytes(v)
    this
  }

  def write(i: MapleString): PacketWriter = {
    write(i.value.length.toShort)
    write(i.value)
  }

  def write(v: String): PacketWriter = {
    buffer.putBytes(v.getBytes)
    this
  }

  def empty(count: Int): PacketWriter = {
    buffer.putBytes(new Array[Byte](count))
    this
  }

  def result: ByteString = buffer.result()
}
