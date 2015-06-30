package MapleScala.PKG4

import java.awt.Point

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
object PKG4Node {
  final val minMapCount = 40
  final val size = 20
}

sealed trait PKG4Node
  extends Iterable[PKG4Node] {
  val reader: PKG4Reader
  val name: String
  val childIndex: Long
  val childCount: Int
  val value: Any

  val isMapped = childCount > PKG4Node.minMapCount

  private val children: Array[PKG4Node] = {
    if (isMapped)
      null
    else
      new Array[PKG4Node](childCount)
  }

  private val childrenMap: mutable.HashMap[String, PKG4Node] = {
    if (isMapped)
      new mutable.HashMap[String, PKG4Node]()
    else
      null
  }

  private def populateChildren(): Unit = {
    if (isMapped && childrenMap.isEmpty)
      for (i <- childIndex until childIndex + childCount)
        childrenMap.put(reader.getNode(i).name, reader.getNode(i))

    else if (!isMapped && children(0) == null)
      for (i <- 0 until childCount)
        children(i) = reader.getNode(i + childIndex)
  }

  def getChild(name: String): Option[PKG4Node] = {
    if (childCount == 0)
      return None

    populateChildren()

    if (isMapped)
      childrenMap.get(name)
    else
      children.find(_.name == name)
  }

  override def iterator: Iterator[PKG4Node] = {
    populateChildren()

    if (isMapped)
      childrenMap.valuesIterator
    else
      children.iterator
  }
}

case class PKG4NoneNode(reader: PKG4Reader, name: String, childIndex: Long, childCount: Int)
  extends PKG4Node {
  reader.skip(8)
  val value = None
}

case class PKG4LongNode(reader: PKG4Reader, name: String, childIndex: Long, childCount: Int)
  extends PKG4Node {
  val value = reader.getLong

  override def iterator = Iterator.empty
}

case class PKG4DoubleNode(reader: PKG4Reader, name: String, childIndex: Long, childCount: Int)
  extends PKG4Node {
  val value = reader.getDouble

  override def iterator = Iterator.empty
}

case class PKG4VectorNode(reader: PKG4Reader, name: String, childIndex: Long, childCount: Int)
  extends PKG4Node {
  val value = new Point(reader.getInt, reader.getInt)

  override def iterator = Iterator.empty
}

case class PKG4StringNode(reader: PKG4Reader, name: String, childIndex: Long, childCount: Int)
  extends PKG4Node {
  val index = reader.getLong
  val value = reader.tables.getString(index)

  override def iterator = Iterator.empty
}
