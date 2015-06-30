package MapleScala.Data

import MapleScala.Client.MapleCharacter
import io.github.nremond.SecureHash
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
class User
  extends Sessionable {
  var id: Int = 0
  var name: String = ""
  var password: String = ""
  var pic: Option[String] = None
  var pin: Option[Int] = None
  var isGM: Boolean = false

  def getCharacter(id: Int): Option[MapleCharacter] = Character.getByUserAndId(this, id)

  def getCharacters: List[MapleCharacter] = Character.listForUser(this)

  def validatePassword(password: String): Boolean = SecureHash.validatePassword(password, this.password)

  def validatePIC(pic: String): Boolean = SecureHash.validatePassword(pic, this.pic.getOrElse(""))

  def validatePIN(pin: Int): Boolean = pin == this.pin.getOrElse(-1) // I figured hashing it would be utterly useless

  def save(): Unit = sql"UPDATE users SET name = $name, password = $password, pic = $pic, pin = $pin, isGM = $isGM WHERE id = $id"
    .update()
    .apply()
}

object User
  extends SQLSyntaxSupport[User]
  with Sessionable {
  def apply(rs: WrappedResultSet): User =
    new User() {
      id = rs.int("id")
      name = rs.string("name")
      password = rs.string("password")
      pic = rs.stringOpt("pic")
      pin = rs.intOpt("pin")
      isGM = rs.boolean("isGM")
    }

  def getById(id: Int): Option[User] = sql"SELECT * FROM users WHERE id = $id"
    .map(rs => User(rs)).single().apply()

  def getByName(name: String): Option[User] = sql"SELECT * FROM users WHERE name = $name"
    .map(rs => User(rs)).single().apply()
}