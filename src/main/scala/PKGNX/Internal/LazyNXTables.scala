package PKGNX.Internal

import java.awt.image.BufferedImage

import PKGNX.NXException
import PKGNX.Util.BufferedReader

/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Aaron Weiss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
class LazyNXTables(val header: NXHeader, val reader: BufferedReader)
  extends NXTables {

  private final val strings: Array[String] = new Array(header.stringCount.toInt)

  def getAudioBuf(index: Long, length: Long): BufferedReader = {
    throw new NXException("Not suported")
  }

  def getImage(index: Long, width: Int, height: Int): BufferedImage = {
    throw new NXException("Not suported")
  }

  def getString(index: Long): String = {
    checkIndex(index)
    val ret: String = strings(index.toInt)
    if (ret != null)
      return ret

    try {
      reader.mark()
      reader.seek(header.stringOffset + index * 8)
      reader.seek(reader.getLong)
      strings(index.toInt) = reader.getString
      strings(index.toInt)
    } finally {
      reader.reset()
    }
  }
}
