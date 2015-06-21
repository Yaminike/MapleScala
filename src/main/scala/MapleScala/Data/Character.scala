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
class Character {
  var id: Int = 0
  var userId: Int = 0
  var name: String = ""
  var world: Byte = 0
  var gender: Boolean = false
  var skinColor: Byte = 0
  var face: Int = 0
  var hair: Int = 0
  var level: Byte = 0
  var job: Short = 0
  var str: Short = 0
  var dex: Short = 0
  var int: Short = 0
  var luk: Short = 0
  var hp: Short = 0
  var maxHp: Short = 0
  var mp: Short = 0
  var maxMp: Short = 0
  var ap: Short = 0
  var sp: Short = 0
  var exp: Int = 0
  var fame: Short = 0
  var gachaExp: Int = 0
  var map: Int = 0
  var spawnpoint: Byte = 0
}

object Character
  extends SQLSyntaxSupport[Character]
  with Sessionable
{
  def apply(rs: WrappedResultSet): Character =
    new Character() {
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
    }

  def listForUser(user: User): List[Character] = sql"SELECT * FROM characters WHERE userId = ${user.id}"
    .map(rs => Character(rs)).list().apply()
}
