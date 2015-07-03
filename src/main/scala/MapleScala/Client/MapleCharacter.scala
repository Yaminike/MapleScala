package MapleScala.Client

import MapleScala.Client.MapleInventory.Types
import MapleScala.Connection.Packets.{PacketWriter, SendOpcode}
import MapleScala.Data
import MapleScala.Data.{Character, WZ}
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
object MapleCharacter {
  def isValidName(name: String): Boolean =
    name.length >= 4 &&
      name.length <= 13 &&
      !WZ.Etc.forbiddenNames.exists(name.toLowerCase.contains) &&
      Data.Character.nameAvailable(name)

  def getDefault: MapleCharacter = new MapleCharacter
}

class MapleCharacter
  extends Character {

  lazy val inventory = MapleInventory.getForCharacter(this)

  def addCharacterEntry(pw: PacketWriter, viewall: Boolean): Unit = {
    addStats(pw)
    addLook(pw, mega = false)
    if (!viewall)
      pw.empty(1)

    // TODO: isGM

    pw.write(true) // World rank enabled?
    pw.empty(16) // TODO: Ranks?
  }

  def getCharacterInfo(channel: Int): PacketWriter = {
    val pw = new PacketWriter()
      .write(SendOpcode.WarpToMap)
      .write(channel)
      .write(true)
      .write(true)
      .empty(2)

    for (i <- 0 until 3)
      pw.write(MapleScala.Helper.random.nextInt())

    addInfo(pw)

    pw.write(System.currentTimeMillis().toMapleTime)
  }

  private def addInfo(pw: PacketWriter): Unit = {
    pw
      .write(-1L)
      .empty(1)

    addStats(pw)

    pw
      .empty(1)
      .write(false) // TODO: LinkedName
      .write(meso)

    addInventoryInfo(pw)
    addSkillInfo(pw)
    addQuestInfo(pw)
    addRingInfo(pw)
    addTeleportInfo(pw)
    addMonsterBookInfo(pw)
    pw.empty(6) // Unk?
  }

  private def addMonsterBookInfo(pw: PacketWriter): Unit = {
    // TODO: MonsterBookInfo
    pw.write(0) // TODO: Cover?
      .empty(3)
  }

  private def addTeleportInfo(pw: PacketWriter): Unit = {
    // TODO: Teleport rocks
    for (i <- 0 until 5)
      pw.write(999999999)
    for (i <- 0 until 10)
      pw.write(999999999)
  }

  private def addRingInfo(pw: PacketWriter): Unit = {
    // TODO: RingInfo
    pw.empty(6)
  }

  private def addQuestInfo(pw: PacketWriter): Unit = {
    // TODO: QuestInfo
    pw.empty(4)
    pw.empty(2)
  }

  private def addSkillInfo(pw: PacketWriter): Unit = {
    // TODO: SkillInfo
    pw.empty(4)
  }

  private def addInventoryInfo(pw: PacketWriter): Unit = {
    pw
      .write(inventory.getSlots(Types.Equip))
      .write(inventory.getSlots(Types.Use))
      .write(inventory.getSlots(Types.Setup))
      .write(inventory.getSlots(Types.Etc))
      .write(inventory.getSlots(Types.Cash))
      .write(-2L.toMapleTime)

    // Normal Equips
    inventory.getItems(Types.Equipped).filter(_.position < 100).foreach(_.addItemInfo(pw))
    pw.empty(2)

    // Cash Equips
    inventory.getItems(Types.Equipped).filter(_.position >= 100).foreach(_.addItemInfo(pw))
    pw.empty(2)

    // Start of the non-equipped items
    inventory.getItems(Types.Equip).foreach(_.addItemInfo(pw))
    pw.empty(4)
    inventory.getItems(Types.Use).foreach(_.addItemInfo(pw))
    pw.empty(1)
    inventory.getItems(Types.Setup).foreach(_.addItemInfo(pw))
    pw.empty(1)
    inventory.getItems(Types.Etc).foreach(_.addItemInfo(pw))
    pw.empty(1)
    inventory.getItems(Types.Cash).foreach(_.addItemInfo(pw))
    pw.empty(1)
  }

  private def addStats(pw: PacketWriter): Unit = {
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

  private def addLook(pw: PacketWriter, mega: Boolean): Unit = {
    pw
      .write(gender)
      .write(skinColor)
      .write(face)
      .write(mega)
      .write(hair)
    addEquips(pw)
  }

  private def addEquips(pw: PacketWriter): Unit = {
    val sep: Byte = -1

    val maskedEquips = inventory.getItems(Types.Equipped).filter(_.position >= 100)

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
    maskedEquips.find(_.position == 111) match {
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
