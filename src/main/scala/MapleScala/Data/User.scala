package MapleScala.Data

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
class User(val id: Int, val name: String, val password: String, val isGM: Boolean, var pin: Option[Int]) {
  implicit val session = AutoSession

  def validatePassword(password: String): Boolean = SecureHash.validatePassword(password, this.password)

  def validatePIN(pin: Int): Boolean = pin == this.pin.getOrElse(-1) // I figured hashing it would be utterly useless

  def updateUser(): Unit = sql"UPDATE users SET name = $name, password = $password, isGM = $isGM, pin = $pin WHERE id = $id"
    .update()
    .apply()
}

object User extends SQLSyntaxSupport[User] {
  implicit val session = AutoSession

  def apply(rs: WrappedResultSet): User =
    new User(rs.int("id"), rs.string("name"), rs.string("password"), rs.boolean("isGM"), rs.intOpt("pin"))

  def getById(id: Int): User = sql"SELECT * FROM users WHERE id = $id"
    .map(rs => User(rs)).single().apply.orNull

  def getByName(name: String): User = sql"SELECT * FROM users WHERE name = $name"
    .map(rs => User(rs)).single().apply.orNull
}