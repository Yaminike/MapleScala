package MapleScala.Data

import MapleScala.Client.{MapleCharacter, MapleJob}
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
class Character {
  var id: Int = 0
  var userId: Int = 0
  var name: String = ""
  var world: Byte = 0
  var gender: Boolean = false
  var skinColor: Byte = 0
  var face: Int = 0
  var hair: Int = 0
  var level: Byte = 1
  var job: Short = MapleJob.Beginner
  var str: Short = 12
  var dex: Short = 5
  var int: Short = 4
  var luk: Short = 4
  var hp: Short = 50
  var maxHp: Short = 50
  var mp: Short = 5
  var maxMp: Short = 5
  var ap: Short = 0
  var sp: Short = 0
  var exp: Int = 0
  var fame: Short = 0
  var gachaExp: Int = 0
  var map: Int = 0
  var spawnpoint: Byte = 0
  var meso: Int = 0

  def save(): Unit = {
    if (id == 0)
      Character.insert(this)
    else
      Character.update(this)
  }
}

object Character
  extends SQLSyntaxSupport[Character]
  with Sessionable {
  def apply(rs: WrappedResultSet): MapleCharacter =
    new MapleCharacter() {
      id = rs.int("id")
      userId = rs.int("userId")
      name = rs.string("name")
      world = rs.byte("world")
      gender = rs.boolean("gender")
      skinColor = rs.byte("skinColor")
      face = rs.int("face")
      hair = rs.int("hair")
      level = rs.byte("level")
      job = rs.short("job")
      str = rs.short("str")
      dex = rs.short("dex")
      int = rs.short("int")
      luk = rs.short("luk")
      hp = rs.short("hp")
      maxHp = rs.short("maxHp")
      mp = rs.short("mp")
      maxMp = rs.short("maxMp")
      ap = rs.short("ap")
      sp = rs.short("sp")
      exp = rs.int("exp")
      fame = rs.short("fame")
      gachaExp = rs.int("gachaExp")
      map = rs.int("map")
      spawnpoint = rs.byte("spawnpoint")
      meso = rs.int("meso")
    }

  def listForUser(user: User): List[MapleCharacter] = sql"SELECT * FROM characters WHERE userId = ${user.id}"
    .map(rs => Character(rs)).list().apply()

  def getById(id: Int): Option[MapleCharacter] = sql"SELECT * FROM characters WHERE id = ${id}"
    .map(rs => Character(rs)).first().apply()

  def getByUserAndId(user: User, id: Int): Option[MapleCharacter] = sql"SELECT * FROM characters WHERE userId = ${user.id} AND id = ${id}"
    .map(rs => Character(rs)).first().apply()

  def nameAvailable(name: String): Boolean = sql"SELECT COUNT(id) AS num FROM characters WHERE name = $name"
    .map(rs => rs.int("num")).first().apply().getOrElse(0) == 0

  def insert(char: Character) = {
    char.id = sql"""
      INSERT INTO characters (
        userId, name, world, gender, skinColor,
        face, hair, level, job, str,
        dex, `int`, luk, hp, maxHp,
        mp, maxMp, ap, sp, exp,
        fame, gachaExp, map, spawnpoint, meso
      ) VALUES (
        ${char.userId}, ${char.name}, ${char.world}, ${char.gender}, ${char.skinColor},
        ${char.face}, ${char.hair}, ${char.level}, ${char.job}, ${char.str},
        ${char.dex}, ${char.int}, ${char.luk}, ${char.hp}, ${char.maxHp},
        ${char.mp}, ${char.maxMp}, ${char.ap}, ${char.sp}, ${char.exp},
        ${char.fame}, ${char.gachaExp}, ${char.map}, ${char.spawnpoint}, ${char.meso})"""
      .updateAndReturnGeneratedKey()
      .apply()
      .toInt
  }

  def update(char: Character) = sql"""
      UPDATE characters SET
        userId = ${char.userId}, name = ${char.name}, world = ${char.world}, gender = ${char.gender}, skinColor = ${char.skinColor},
        face = ${char.face}, hair = ${char.hair}, level = ${char.level}, job = ${char.job}, str = ${char.str},
        dex = ${char.dex}, `int` = ${char.int}, luk = ${char.luk}, hp = ${char.hp}, maxHp = ${char.maxHp},
        mp = ${char.mp}, maxMp = ${char.maxMp}, ap = ${char.ap}, sp = ${char.sp}, exp = ${char.exp},
        fame = ${char.fame}, gachaExp = ${char.gachaExp}, map = ${char.map}, spawnpoint = ${char.spawnpoint}, meso = ${char.meso}
      WHERE id = ${char.id}"""
    .update()
    .apply()
}
