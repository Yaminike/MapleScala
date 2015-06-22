package PKGNX

import scala.collection.mutable.HashMap

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

object NXNode {
  final val emptyIterator = new EmptyNodeIterator
  final val minMapCount: Int = 41
  final val nodeSize: Int = 20
}

/**
 * The basic information container for the NX file format.
 */
abstract class NXNode(val name: String,
                      val file: NXFile,
                      val childIndex: Int,
                      val childCount: Int,
                      val skipConstruct: Boolean)
  extends Iterable[NXNode] {

  private var children: Array[NXNode] = _
  private var childrenMap: HashMap[String, NXNode] = _

  /**
   * Sets up the basic information for the NXNode.
   *
   * @param name       the name of the node
   * @param file       the file the node is from
   * @param childIndex the index of the first child of the node
   * @param childCount the number of children
   */
  def this(name: String, file: NXFile, childIndex: Int, childCount: Int) = {
    this(name, file, childIndex, childCount, false)
    if (childCount >= NXNode.minMapCount) {
      childrenMap = new HashMap[String, NXNode]
    } else if (childCount > 0) {
      children = new Array[NXNode](childCount)
    }
  }

  /**
   * Populates the children Map for this node.
   */
  def populateChildren(): Unit = {
    if (childCount == 0)
      return
    if (childrenMap != null && childrenMap.isEmpty) {
      for (i <- childIndex until childIndex + childCount)
        childrenMap.put(file.getNode(i).name, file.getNode(i))
    } else if (children(0) == null) {
      for (i <- 0 until childCount)
        children(i) = file.getNode(i + childIndex)
    }
  }

  /**
   * Gets the value of this node universally.
   *
   * @return the value as an Any
   */
  def get(): Any

  /**
   * Gets a child node by name. Returns null if child is not present.
   *
   * @param name the name of the child
   * @return the child NXNode
   */
  def getChild[T <: NXNode](name: String): T = {
    if (childCount != 0)
      return searchChild(name).asInstanceOf[T]
    null.asInstanceOf[T]
  }

  /**
   * Determines whether or not this node has a child by the specified name.
   *
   * @param name the name of the child
   * @return whether or not this node has a child by the specified name
   */
  def hasChild(name: String): Boolean = getChild(name) != null

  /**
   * Searches for a specific child node by name. Internally, this deals with how the children are stored.
   *
   * @param name the name of the child to find
   * @return the found child or null, if it doesn't exist
   */
  protected def searchChild(name: String): NXNode = {
    if (childCount == 0)
      return null
    else if ((children != null && children(0) == null) || (childrenMap != null && childrenMap.isEmpty))
      this.populateChildren()
    if (childrenMap != null)
      return childrenMap.getOrElse(name, null)

    var min: Int = 0
    var max: Int = childCount - 1
    var minVal: String = children(min).name
    var maxVal: String = children(max).name

    while (true) {
      if (name.compareTo(minVal) <= 0) {
        if (name.equals(minVal))
          return children(min)
        else
          return null
      }

      if (name.compareTo(maxVal) >= 0) {
        if (name.equals(maxVal))
          return children(max)
        else
          return null
      }

      val pivot = (min + max) >> 1
      val pivotVal = children(pivot).name

      if (name.compareTo(pivotVal) > 0) {
        min = pivot + 1
        max -= 1
      } else if (name.equals(pivotVal)) {
        return children(pivot)
      } else {
        min += 1
        max = pivot - 1
      }

      minVal = children(min).name
      maxVal = children(max).name
    }
    null
  }

  override def toString(): String = name

  override def equals(any: Any): Boolean = {
    if (any == null)
      return false
    else if (!any.isInstanceOf[NXNode])
      return false

    val n = any.asInstanceOf[NXNode]
    any == this || (
      n.name.equals(name) &&
        n.childCount == childCount &&
        n.childIndex == childIndex &&
        ((n.get() == null && get() == null) || n.get() == get())
      )
  }

  override def iterator: Iterator[NXNode] = {
    if (childCount == 0)
      return NXNode.emptyIterator
    else if ((children != null && children(0) == null) || (childrenMap != null && childrenMap.isEmpty))
      this.populateChildren()

    if (childrenMap == null)
      children.iterator
    else
      childrenMap.valuesIterator
  }
}
