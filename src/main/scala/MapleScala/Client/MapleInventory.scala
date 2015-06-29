package MapleScala.Client

import MapleScala.Data.Item

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
object MapleInventory {

  def getForCharacter(character: MapleCharacter): MapleInventory = {
    val items = Item.getForCharacter(character)
    val inv = new MapleInventory(character)
    for (value <- Types.values) {
      inv.addItems(value)(items.filter(_.invType == value.id))
    }
    inv
  }

  object Types extends Enumeration {
    val Equipped, Equip, Use, Setup, Etc, Cash = Value
  }

}

class MapleInventory(character: MapleCharacter) {

  import MapleInventory.Types._

  private val items: Map[MapleInventory.Types.Value, mutable.MutableList[MapleItem]] = Map(
    Equipped -> mutable.MutableList[MapleItem](),
    Equip -> mutable.MutableList[MapleItem](),
    Use -> mutable.MutableList[MapleItem](),
    Setup -> mutable.MutableList[MapleItem](),
    Etc -> mutable.MutableList[MapleItem](),
    Cash -> mutable.MutableList[MapleItem]()
  )

  private val slots: Map[MapleInventory.Types.Value, Byte] = Map(
    Equip -> 24,
    Use -> 24,
    Setup -> 24,
    Etc -> 24,
    Cash -> 24
  )

  def addItem(invType: MapleInventory.Types.Value)(mapleItem: MapleItem): Boolean = {
    def hasPos(position: Int): Boolean = items(invType).exists(_.position == position)

    if (invType == Equipped) {
      if (hasPos(mapleItem.position))
        return false
    } else {
      if (hasPos(mapleItem.position)) {
        val pos: Option[Int] = (0 until slots(invType)).find(!hasPos(_))
        if (pos.isEmpty)
          return false
        mapleItem.position = pos.get.toByte
      }
    }

    mapleItem.invType = invType.id
    items(invType) += mapleItem
    true
  }

  private def addItems(invType: MapleInventory.Types.Value)(list: Seq[MapleItem]): Unit = items(invType) ++= list

  def getItems(invType: MapleInventory.Types.Value): mutable.MutableList[MapleItem] = items(invType)

  def save(): Unit = {
    for (list <- items.values; item <- list) {
      item.characterId = character.id
      item.save()
    }
  }
}
