package MapleScala.Data

import MapleScala.Client.MapleItem
import scalikejdbc._

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
class Item {
  var id: Long = 0
  var characterId: Int = 0
  var itemId: Int = 0
  var invType: Int = 0
  var position: Byte = 0
  var amount: Short = 0
  var stats: Option[Itemstats] = None

  def save(): Unit = {
    if (id == 0) {
      Item.insert(this)
      for (stats <- this.stats) {
        stats.itemId = id
        Itemstats.insert(stats)
      }
    } else {
      Item.update(this)
      for (stats <- this.stats)
        Itemstats.update(stats)
    }
  }
}

object Item
  extends SQLSyntaxSupport[Item]
  with Sessionable {

  def apply(rs: WrappedResultSet): MapleItem =
    new MapleItem(rs.int("id"), rs.byte("position"), rs.short("amount")) {
      characterId = rs.int("characterId")
      itemId = rs.int("itemId")
      stats = Itemstats.getForItem(id)
      invType = rs.int("type")
    }

  def getForCharacter(character: Character): List[MapleItem] =
    sql"SELECT * FROM items WHERE characterId = ${character.id}"
      .map(rs => Item(rs))
      .list()
      .apply()

  def insert(item: Item): Unit = {
    item.id = sql"""
      INSERT INTO items (
        characterId, itemId, type, position, amount
      ) VALUES (
        ${item.characterId}, ${item.itemId}, ${item.invType}, ${item.position}, ${item.amount}
      )"""
      .updateAndReturnGeneratedKey()
      .apply()
  }

  def update(item: Item): Unit = sql"""
    UPDATE items SET
      characterId = ${item.characterId}, itemId = ${item.itemId},  type = ${item.invType}, position = ${item.position}, amount = ${item.amount}
    WHERE id = ${item.id}"""
    .update()
    .apply()
}