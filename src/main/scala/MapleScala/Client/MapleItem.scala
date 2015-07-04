package MapleScala.Client

import MapleScala.Connection.Packets.PacketWriter
import MapleScala.Data
import MapleScala.Data.{Item, Itemstats}
import MapleScala.Util.Extensions._

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
object MapleItem {

  object Flags {
    final val Lock: Byte = 0x01
    final val Spikes: Byte = 0x02
    final val Cold: Byte = 0x04
    final val Untradeable: Byte = 0x08
    final val Karma: Byte = 0x10
    final val PetCome: Byte = 0x80.toByte
  }

  def getDefault(itemId: Int, position: Byte): MapleItem = {
    val ret = new MapleItem(itemId, position, 1)
    for (info <- Data.WZ.Character.itemInfo.get(itemId)) {
      ret.stats = Some(new Itemstats {
        str = info.getOrElse("incSTR", 0l).toShort
        dex = info.getOrElse("incDEX", 0l).toShort
        int = info.getOrElse("incINT", 0l).toShort
        luk = info.getOrElse("incLUK", 0l).toShort

        wAtt = info.getOrElse("incPAD", 0l).toShort
        wDef = info.getOrElse("incPDD", 0l).toShort
        mAtt = info.getOrElse("incMAD", 0l).toShort
        mDef = info.getOrElse("incMDD", 0l).toShort

        acc = info.getOrElse("incACC", 0l).toShort
        eva = info.getOrElse("incEVA", 0l).toShort
        speed = info.getOrElse("incSpeed", 0l).toShort
        jump = info.getOrElse("incJump", 0l).toShort
        hp = info.getOrElse("incMHP", 0l).toShort
        mp = info.getOrElse("incMMP", 0l).toShort
        slots = info.getOrElse("tuc", 0l).toByte

        if (info.getOrElse("fs", 0l) > 0) {
          flags |= Flags.Spikes
        }

        if (isDropRestricted(info)) {
          flags |= Flags.Untradeable
        }
      })

    }

    ret
  }

  def isDropRestricted(info: Map[String, Long]): Boolean = info.getOrElse("tradeBlock", 0l) == 1 || info.getOrElse("quest", 0l) == 1

  def isDropRestricted(itemId: Int): Boolean = {
    Data.WZ.Character.itemInfo.get(itemId) match {
      case Some(info) => isDropRestricted(info)
      case None => false
    }
  }
}

class MapleItem(pItemId: Int, pPosition: Byte, pAmount: Short)
  extends Item {

  id = pItemId
  position = pPosition
  amount = pAmount

  def isEquip: Boolean = stats.nonEmpty

  def addItemInfo(pw: PacketWriter, zeroPosition: Boolean = false): Unit = {
    var pos: Int = position
    if (pos > 100)
      pos -= 100

    if (!zeroPosition) {
      if (isEquip)
        pw.write(pos.toShort)
          .write(1.toByte)
      else
        pw.write(pos.toByte)
          .write(invType.toByte)
    }

    pw
      .write(itemId)
      .write(false) // TODO: isCash
      .write(-1L.toMapleTime) // TODO: addExpirationTime

    // TODO: isPet

    if (!isEquip) {
      pw
        .write(amount)
        .write("".toMapleString) // TODO: Owner
        .write(0.toShort) // TODO: figure out 'flag'

      // TODO: isRechargeable

      return
    }

    for (stats <- this.stats) {
      pw.write(stats.slots)
        .write(0.toByte) // TODO: level
        .write(stats.str)
        .write(stats.dex)
        .write(stats.int)
        .write(stats.luk)
        .write(stats.hp)
        .write(stats.mp)
        .write(stats.wAtt)
        .write(stats.mAtt)
        .write(stats.wDef)
        .write(stats.mDef)
        .write(stats.acc)
        .write(stats.eva)
        .write(0.toShort) // TODO: hands
        .write(stats.speed)
        .write(stats.jump)
        .write("".toMapleString) // TODO: Owner
        .write(stats.flags.toShort)

        // TODO: isCash
        .empty(1)
        .write(1.toByte) // TODO: Level
        .write(0.toShort)
        .write(0.toShort)
        .write(0)
        .write(0L)
        .write(-2L.toMapleTime)
        .write(-1)
    }
  }
}
