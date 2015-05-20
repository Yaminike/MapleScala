package MapleScala.Crypto

import java.nio.{ByteBuffer, ByteOrder}

import org.scalatest._

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
class FastAESSpec extends FlatSpec with Matchers {
  "ToInt" should "validate" in {
    val key: Array[Byte] = Array(0xEF, 0xBE, 0xAD, 0xDE).map(_.toByte)
    FastAES.ToInt(key, 0) should be(0xDEADBEEF)
  }

  "Shift" should "validate" in {
    FastAES.Shift(FastAES.Shift(0xDEADBEEF, 8), 24) should be(0xDEADBEEF)
  }

  "Subword" should "validate" in {
    FastAES.SubWord(0xDEADBEEF) should be(0x1D95AEDF)
  }

  "generateWorkingKey" should "validate" in {
    val wk = FastAES.generateWorkingKey
    val expected: Array[Int] = Array(
      0x000000B4, 0x00000052,
      0x006363CB, 0x63FBFB6A,
      0x02FB0FB1, 0x770F7695
    )
    for (i <- 0 until expected.length) {
      wk(i)(3) should be(expected(i))
    }
  }

  "transform" should "validate" in {
    val buffer: Array[Int] = new Array(4)
    val result: ByteBuffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    for (i <- 0 until 10)
      FastAES.TransformBlock(buffer)
    buffer.foreach(result.putInt)
    MapleScala.Helper.toHex(result.array()) should be("EC EB 61 6F 25 0A 15 A1 8D 1A 3B 80 EC 5E 49 36")
  }
}
