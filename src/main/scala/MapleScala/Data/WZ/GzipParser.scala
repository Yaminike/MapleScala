package MapleScala.Data.WZ

import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream

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
object GzipParser {
  def readGzip(stream: InputStream): ByteBuffer = {
    val size = getGzipSize(stream)
    val gzip = new GZIPInputStream(stream)
    val result = ByteBuffer.allocate(size)

    var bufferSize = 0x200
    if (bufferSize > size)
      bufferSize = size

    val temp = new Array[Byte](bufferSize)
    var n = gzip.read(temp)
    while (n >= 0) {
      result.put(temp, 0, n)
      n = gzip.read(temp)
    }

    result
  }

  def getGzipSize(stream: InputStream): Int = {
    stream.mark(stream.available())
    stream.skip(stream.available() - 4)
    val b4 = stream.read()
    val b3 = stream.read()
    val b2 = stream.read()
    val b1 = stream.read()
    stream.reset()
    (b1 << 24) | (b2 << 16) | (b3 << 8) | b4
  }
}
