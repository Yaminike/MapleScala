package MapleScala.Client

import MapleScala.Client.MapleInventory.Types
import MapleScala.Connection.Packets.{PacketWriter, SendOpcode}
import MapleScala.Data
import MapleScala.Data.{Character, WZ}

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
object MapleCharacter {
  def isValidName(name: String): Boolean =
    name.length >= 4 &&
      name.length <= 13 &&
      !WZ.Etc.forbiddenNames.exists(f => name.toLowerCase.contains(f)) &&
      Data.Character.nameAvailable(name)

  def getDefault: MapleCharacter = new MapleCharacter
}

class MapleCharacter
  extends Character {

  var inventory = new MapleInventory(this)

  def getCharInfo(channel: Int): PacketWriter = {
    new PacketWriter()
      .write(SendOpcode.WarpToMap)
      .write(channel)
      .write(true)
      .write(true)
      .empty(2)
      .write(MapleScala.Helper.random.nextInt())
      .write(MapleScala.Helper.random.nextInt())
      .write(MapleScala.Helper.random.nextInt())
  }

  def addCharEntry(pw: PacketWriter, viewall: Boolean): Unit = {
    addCharStats(pw)
    addCharLook(pw, mega = false)
    if (!viewall)
      pw.empty(1)

    // TODO: isGM

    pw.write(true) // World rank enabled?
    pw.empty(16) // TODO: Ranks?
  }

  def addCharStats(pw: PacketWriter): Unit = {
    pw
      .write(id)
      .write(name.padTo(13, '\0'))
      .write(gender)
      .write(skinColor)
      .write(face)
      .write(hair)
      .empty(24) // TODO: pets
      .write(level)
      .write(job)
      .write(str)
      .write(dex)
      .write(int)
      .write(luk)
      .write(hp)
      .write(maxHp)
      .write(mp)
      .write(maxMp)
      .write(ap)
      .write(sp)
      .write(exp)
      .write(fame)
      .write(gachaExp)
      .write(map)
      .write(spawnpoint)
      .empty(4)
  }

  def addCharLook(pw: PacketWriter, mega: Boolean): Unit = {
    pw
      .write(gender)
      .write(skinColor)
      .write(face)
      .write(mega)
      .write(hair)
    addCharEquips(pw)
  }

  def addCharEquips(pw: PacketWriter): Unit = {
    val sep: Byte = -1

    val maskedEquips = inventory.getItems(Types.Equipped).filter(item => item.position >= 100)

    val equips = inventory.getItems(Types.Equipped).map(item => {
      val mask = maskedEquips.find(x => x.position - 100 == item.position && x.position != 111)
      val ret = mask.getOrElse(item)
      ret.position = item.position
      ret
    })

    // Normal equips
    for (item <- equips) {
      pw
        .write(item.position)
        .write(item.itemId)
    }
    pw.write(sep)

    // Masked equips
    for (item <- maskedEquips) {
      pw
        .write(item.position - 100)
        .write(item.itemId)
    }
    pw.write(sep)

    // Cash weapon
    maskedEquips.find(x => x.position == 111) match {
      case Some(weapon) => pw.write(weapon.itemId)
      case None => pw.write(0)
    }

    // TODO: pet equips
    pw.empty(12)
  }

  override def save(): Unit = {
    super.save()
    inventory.save()
  }
}
