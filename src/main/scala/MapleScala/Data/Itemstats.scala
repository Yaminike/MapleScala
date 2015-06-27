package MapleScala.Data

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
class Itemstats {
  var itemId: Long = 0

  var str: Short = 0
  var dex: Short = 0
  var int: Short = 0
  var luk: Short = 0

  var wAtt: Short = 0
  var wDef: Short = 0
  var mAtt: Short = 0
  var mDef: Short = 0

  var acc: Short = 0
  var eva: Short = 0
  var speed: Short = 0
  var jump: Short = 0
  var hp: Short = 0
  var mp: Short = 0
  var slots: Byte = 0
  var flags: Int = 0
}

object Itemstats
  extends SQLSyntaxSupport[Itemstats]
  with Sessionable {

  def apply(rs: WrappedResultSet): Itemstats =
    new Itemstats {
      itemId = rs.long("itemId")

      str = rs.short("str")
      dex = rs.short("dex")
      int = rs.short("int")
      luk = rs.short("luk")

      wAtt = rs.short("wAtt")
      wDef = rs.short("wDef")
      mAtt = rs.short("mAtt")
      mDef = rs.short("mDef")

      acc = rs.short("acc")
      eva = rs.short("eva")
      speed = rs.short("speed")
      jump = rs.short("jump")
      hp = rs.short("hp")
      mp = rs.short("mp")
      slots = rs.byte("slots")
      flags = rs.byte("flags")
    }

  def getForItem(id: Long): Option[Itemstats] = sql"SELECT * FROM itemstats WHERE itemId = $id"
    .map(rs => Itemstats(rs)).first().apply()

  def insert(stats: Itemstats): Unit = sql"""
    INSERT INTO itemstats VALUES (
      ${stats.itemId}, ${stats.str}, ${stats.dex}, ${stats.int}, ${stats.luk},
      ${stats.wAtt}, ${stats.wDef}, ${stats.mAtt}, ${stats.mDef}, ${stats.acc},
      ${stats.eva}, ${stats.speed}, ${stats.jump}, ${stats.hp}, ${stats.mp},
      ${stats.slots}, ${stats.flags}
    )"""
    .update()
    .apply()

  def update(stats: Itemstats): Unit = sql"""
    UPDATE itemstats SET
      str = ${stats.str}, dex = ${stats.dex}, `int` = ${stats.int}, luk = ${stats.luk}, wAtt = ${stats.wAtt},
      wDef = ${stats.wDef}, mAtt = ${stats.mAtt}, mDef = ${stats.mDef}, acc = ${stats.acc}, eva = ${stats.eva},
      speed = ${stats.speed}, jump = ${stats.jump}, hp = ${stats.hp}, mp = ${stats.mp}, slots = ${stats.slots},
      flags = ${stats.flags}
    WHERE itemId = ${stats.itemId}"""
    .update()
    .apply()
}
