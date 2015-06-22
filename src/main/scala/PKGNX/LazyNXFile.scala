package PKGNX

import java.nio.ByteBuffer

import PKGNX.Internal.{LazyNXTables, NXHeader, NXTables}
import PKGNX.Util.{BufferedReader, NodeParser}

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
class LazyNXFile(val buffer: ByteBuffer)
  extends NXFile {

  final val reader: BufferedReader = new BufferedReader(buffer)
  override var header: NXHeader = new NXHeader(this, reader)
  override var tables: NXTables = new LazyNXTables(header, reader)
  final val nodes: Array[NXNode] = new Array[NXNode](header.nodeCount.toInt)

  def getNode(index: Int): NXNode = {
    val ret = nodes(index)
    if (ret != null)
      return ret

    reader.seek(header.nodeOffset + index * NXNode.nodeSize)
    nodes(index) = NodeParser.parseNode(this, reader)

    nodes(index)
  }
}
