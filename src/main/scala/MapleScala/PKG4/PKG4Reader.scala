package MapleScala.PKG4

import java.nio.ByteBuffer

import MapleScala.Util.LittleEndianReader

import scala.collection.mutable

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
final class PKG4Reader(override val buffer: ByteBuffer)
  extends LittleEndianReader(buffer) {

  val header = new PKG4Header(this)
  val tables = new PKG4Tables(this)
  val nodes = new mutable.HashMap[Long, PKG4Node]

  private def parseNode(index: Long): PKG4Node = {
    seek(header.nodeOffset + index * PKG4Node.size)

    val length: Long = getUInt
    val name: String = tables.getString(length)
    val childIndex: Long = getUInt
    val childCount: Int = getUShort
    val nodeType: Int = getUShort

    nodeType match {
      case 0 => new PKG4NoneNode(this, name, childIndex, childCount)
      case 1 => new PKG4LongNode(this, name, childIndex, childCount)
      case 2 => new PKG4DoubleNode(this, name, childIndex, childCount)
      case 3 => new PKG4StringNode(this, name, childIndex, childCount)
      case 4 => new PKG4VectorNode(this, name, childIndex, childCount)
    }
  }

  def getNode(index: Long): PKG4Node =
    nodes.getOrElse(index, {
      nodes.put(index, parseNode(index))
      nodes(index)
    })

  def resolve(path: String): Option[PKG4Node] = {
    if (path.equals("/"))
      Some(getNode(0))
    else
      resolve(path.split('/'))
  }

  def resolve(path: Seq[String]): Option[PKG4Node] = {
    var cursor: Option[PKG4Node] = Some(getNode(0))
    path.foldLeft(cursor) { (z, i) =>
      if (cursor.nonEmpty)
        cursor = z.get.getChild(i)
      cursor
    }
  }
}
