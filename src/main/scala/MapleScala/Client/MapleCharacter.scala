package MapleScala.Client

import MapleScala.Connection.Packets.PacketWriter
import MapleScala.Data.Character

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
class MapleCharacter
  extends Character {

  def addCharEntry(pw: PacketWriter, viewall: Boolean): Unit ={
    addCharStats(pw)
    addCharLook(pw, false)
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

    pw
      .write(sep)
      .write(sep)
      .empty(16) // TODO: pet equips, cash weapon
  }
}
