package MapleScala.Crypto

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
class CipherHelperSpec extends FlatSpec with Matchers {
  private final val cipher = new CipherHelper()

  "transform" should "validate" in {
    val buffer: Array[Byte] = new Array(16)
    cipher.transform(buffer, 16, 0xDEADBEEF)
    MapleScala.Helper.toHex(buffer) should be("9B E5 2F FE 37 3B 93 00 50 E0 09 FB FB 3F 97 A9")
  }
}
