package PKGNX.Internal

import PKGNX.Util.BufferedReader
import PKGNX.{NXException, NXFile}

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
class NXHeader(val file: NXFile, val reader: BufferedReader) {
  reader.seek(0)

  final val magic: String = reader.getString(4)
  if (!magic.equals("PKG4"))
    throw new NXException("Cannot read file")
  final val nodeCount: Long = reader.getUInt
  final val nodeOffset: Long = reader.getLong
  final val stringCount: Long = reader.getUInt
  final val stringOffset: Long = reader.getLong
  final val bitmapCount: Long = reader.getUInt
  final val bitmapOffset: Long = reader.getLong
  final val soundCount: Long = reader.getUInt
  final val soundOffset: Long = reader.getLong
}
