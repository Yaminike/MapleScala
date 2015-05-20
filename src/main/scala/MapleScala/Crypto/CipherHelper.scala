package MapleScala.Crypto

import java.nio.{ByteBuffer, ByteOrder}

import MapleScala.Connection.Client
import MapleScala.Connection.Packets.PacketReader
import akka.io.Tcp.Abort
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
class CipherHelper(final val client: Client) {
  @volatile final var RIV: Int = MapleScala.Helper.random.nextInt()
  @volatile final var SIV: Int = MapleScala.Helper.random.nextInt()
  private final val DefaultKey: Int = 0xC65053F2
  private final val GameVersion = 83

  // TODO: multiple packet support?
  def decrypt(in: ByteBuffer): PacketReader = {
    in.clear() // resets the position

    // Copy to new array
    val length: Int = getPacketLength(in.getShort, in.getShort)
    val result: Array[Byte] = new Array[Byte](length)
    in.get(result, 0, length)

    // AES transform
    transform(result, length, RIV)
    RIV = shuffle(RIV)

    // Shanda transform
    decryptShanda(result)

    // Create result
    new PacketReader(ByteBuffer.wrap(result).order(ByteOrder.LITTLE_ENDIAN))
  }

  def encrypt(in: ByteString): ByteString = {
    val data: Array[Byte] = new Array(in.length)
    in.copyToArray(data)

    // Create packetheader
    val header = new ByteStringBuilder
    header.putInt(getPacketHeader(data))(ByteOrder.LITTLE_ENDIAN)

    // Shanda transform
    encryptShanda(data)

    // AES transform
    transform(data, in.length, SIV)
    SIV = shuffle(SIV)

    // Combine header and data
    header.putBytes(data)
    header.result()
  }

  private[Crypto] def transform(in: Array[Byte], read: Int, vector: Int): Unit = {
    var length: Int = 0x5B0
    var start: Int = 0
    var cur: Int = 0
    var remaining: Int = read
    val realIV = new Array[Int](4)

    while (remaining > 0) {
      for (i <- 0 until 4)
        realIV(i) = vector

      if (remaining < length)
        length = remaining

      for (i <- start until start + length) {
        if ((i - start) % 16 == 0)
          FastAES.TransformBlock(realIV)

        cur = (i - start) % 16
        in(i) = (in(i) ^ ((realIV(cur / 4) >>> (cur % 4 * 8)) & 0xFF)).toByte
      }
      start += length
      remaining -= length
      length = 0x5B4
    }
  }

  private def getPacketLength(left: Short, right: Short): Int = {
    val tmp = left ^ right
    ((tmp << 8) & 0xFF00) | ((tmp >>> 8) & 0xFF)
  }

  private def getPacketHeader(data: Array[Byte]): Int = {
    val left = (-(GameVersion + 1) ^ ((SIV << 16) >>> 16)).toShort
    val right = (left ^ data.length).toShort
    (left << 16) | right
  }

  private def decryptShanda(buffer: Array[Byte]): Unit = {
    var xorKey: Byte = 0
    var save: Byte = 0
    var len: Byte = 0
    var tmp: Byte = 0
    for (pass <- 0 until 3) {
      xorKey = 0
      save = 0
      len = (buffer.length & 0xFF).toByte
      for (i <- buffer.length - 1 to 0 by -1) {
        tmp = (ROL(buffer(i), 3) ^ 0x13).toByte
        save = tmp
        tmp = ROR(((xorKey ^ tmp) - len).toByte, 4)
        xorKey = save
        buffer(i) = tmp
        len = (len - 1).toByte
      }

      xorKey = 0
      len = (buffer.length & 0xFF).toByte
      for (i <- buffer.indices) {
        tmp = ROL((~(buffer(i) - 0x48)).toByte, len & 0xFF)
        save = tmp
        tmp = ROR(((xorKey ^ tmp) - len).toByte, 3)
        xorKey = save
        buffer(i) = tmp
        len = (len - 1).toByte
      }
    }
  }

  private def encryptShanda(buffer: Array[Byte]): Unit = {
    var xorKey: Byte = 0
    var save: Byte = 0
    var len: Byte = 0
    var tmp: Byte = 0
    for (pass <- 0 until 3) {
      xorKey = 0
      save = 0
      len = (buffer.length & 0xFF).toByte
      for (i <- buffer.indices) {
        tmp = ((ROL(buffer(i), 3) + len).toByte ^ xorKey).toByte
        xorKey = tmp
        tmp = (((~ROR(tmp, len & 0xFF)) & 0xFF) + 0x48).toByte
        buffer(i) = tmp
        len = (len - 1).toByte
      }
      xorKey = 0
      save = 0
      len = (buffer.length & 0xFF).toByte
      for (i <- buffer.length - 1 to 0 by -1) {
        tmp = (xorKey ^ (len + ROL(buffer(i), 4))).toByte
        xorKey = tmp
        tmp = ROR((tmp ^ 0x13).toByte, 3)
        buffer(i) = tmp
        len = (len - 1).toByte
      }
    }
  }

  def ROL(byte: Byte, n: Int): Byte = {
    val tmp = (byte & 0xFF) << (n & 7)
    (tmp | tmp >>> 8).toByte
  }

  def ROR(byte: Byte, n: Int): Byte = {
    val tmp = (byte & 0xFF) << (8 - (n & 7))
    (tmp | tmp >>> 8).toByte
  }

  private def shuffle(vector: Int): Int = {
    var holder: Int = DefaultKey
    val pIv = getHolder(vector)
    var pKey = getHolder(holder)

    for (i <- 0 until 4) {
      pKey(0) = (pKey(0) + (getTable(pKey(1)) - pIv(i))).toByte
      pKey(1) = (pKey(1) - (pKey(2) ^ getTable(pIv(i)))).toByte
      pKey(2) = (pKey(2) ^ (pIv(i) + getTable(pKey(3)))).toByte
      pKey(3) = (pKey(3) - pKey(0) + getTable(pIv(i))).toByte

      holder = setHolder(pKey)
      holder = holder << 3 | holder >>> (32 - 3)
      pKey = getHolder(holder)
    }

    setHolder(pKey)
  }

  private def getHolder(int: Int): Array[Byte] = {
    val h0: Byte = (int & 0xFF).toByte
    val h1: Byte = ((int >>> 8) & 0xFF).toByte
    val h2: Byte = ((int >>> 16) & 0xFF).toByte
    val h3: Byte = ((int >>> 24) & 0xFF).toByte
    Array(h0, h1, h2, h3)
  }

  private def setHolder(array: Array[Byte]): Int = ((array(3) & 0xFF) << 24) | ((array(2) & 0xFF) << 16) | ((array(1) & 0xFF) << 8) | (array(0) & 0xFF)

  private def getTable(byte: Byte): Byte = {
    var index: Int = byte
    if (index < 0)
      index = 0x100 + index
    Table(index)
  }

  private final val Table: Array[Byte] = Array(
    0xEC, 0x3F, 0x77, 0xA4, 0x45, 0xD0, 0x71, 0xBF, 0xB7, 0x98, 0x20, 0xFC, 0x4B, 0xE9, 0xB3, 0xE1,
    0x5C, 0x22, 0xF7, 0x0C, 0x44, 0x1B, 0x81, 0xBD, 0x63, 0x8D, 0xD4, 0xC3, 0xF2, 0x10, 0x19, 0xE0,
    0xFB, 0xA1, 0x6E, 0x66, 0xEA, 0xAE, 0xD6, 0xCE, 0x06, 0x18, 0x4E, 0xEB, 0x78, 0x95, 0xDB, 0xBA,
    0xB6, 0x42, 0x7A, 0x2A, 0x83, 0x0B, 0x54, 0x67, 0x6D, 0xE8, 0x65, 0xE7, 0x2F, 0x07, 0xF3, 0xAA,
    0x27, 0x7B, 0x85, 0xB0, 0x26, 0xFD, 0x8B, 0xA9, 0xFA, 0xBE, 0xA8, 0xD7, 0xCB, 0xCC, 0x92, 0xDA,
    0xF9, 0x93, 0x60, 0x2D, 0xDD, 0xD2, 0xA2, 0x9B, 0x39, 0x5F, 0x82, 0x21, 0x4C, 0x69, 0xF8, 0x31,
    0x87, 0xEE, 0x8E, 0xAD, 0x8C, 0x6A, 0xBC, 0xB5, 0x6B, 0x59, 0x13, 0xF1, 0x04, 0x00, 0xF6, 0x5A,
    0x35, 0x79, 0x48, 0x8F, 0x15, 0xCD, 0x97, 0x57, 0x12, 0x3E, 0x37, 0xFF, 0x9D, 0x4F, 0x51, 0xF5,
    0xA3, 0x70, 0xBB, 0x14, 0x75, 0xC2, 0xB8, 0x72, 0xC0, 0xED, 0x7D, 0x68, 0xC9, 0x2E, 0x0D, 0x62,
    0x46, 0x17, 0x11, 0x4D, 0x6C, 0xC4, 0x7E, 0x53, 0xC1, 0x25, 0xC7, 0x9A, 0x1C, 0x88, 0x58, 0x2C,
    0x89, 0xDC, 0x02, 0x64, 0x40, 0x01, 0x5D, 0x38, 0xA5, 0xE2, 0xAF, 0x55, 0xD5, 0xEF, 0x1A, 0x7C,
    0xA7, 0x5B, 0xA6, 0x6F, 0x86, 0x9F, 0x73, 0xE6, 0x0A, 0xDE, 0x2B, 0x99, 0x4A, 0x47, 0x9C, 0xDF,
    0x09, 0x76, 0x9E, 0x30, 0x0E, 0xE4, 0xB2, 0x94, 0xA0, 0x3B, 0x34, 0x1D, 0x28, 0x0F, 0x36, 0xE3,
    0x23, 0xB4, 0x03, 0xD8, 0x90, 0xC8, 0x3C, 0xFE, 0x5E, 0x32, 0x24, 0x50, 0x1F, 0x3A, 0x43, 0x8A,
    0x96, 0x41, 0x74, 0xAC, 0x52, 0x33, 0xF0, 0xD9, 0x29, 0x80, 0xB1, 0x16, 0xD3, 0xAB, 0x91, 0xB9,
    0x84, 0x7F, 0x61, 0x1E, 0xCF, 0xC5, 0xD1, 0x56, 0x3D, 0xCA, 0xF4, 0x05, 0xC6, 0xE5, 0x08, 0x49
  ).map(_.toByte)
}
