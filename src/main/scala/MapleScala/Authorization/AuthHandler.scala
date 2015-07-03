package MapleScala.Authorization

import MapleScala.Data.User
import MapleScala.Util.ExpirationMap
import akka.actor.{Actor, Props}

import scala.collection.mutable

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
object AuthHandler {
  def create: Props = Props(new AuthHandler())
}

class AuthHandler extends Actor {
  val users: mutable.HashMap[Int, AuthHolder] = mutable.HashMap()
  var migrations = new ExpirationMap[Int, MigrationHolder](10 * 1000)

  def receive = {
    case req: AuthRequest.Login =>
      User.getByName(req.username) match {
        case Some(user) =>
          if (!user.validatePassword(req.password)) {
            sender ! new AuthResponse.Login(4, Some(user))
          } else {
            users += user.id -> new AuthHolder {
              status += AuthStatus.LoggedIn
            }
            sender ! new AuthResponse.Login(0, Some(user))
          }
        case None => sender ! new AuthResponse.Login(5, None)
      }

    case req: AuthRequest.Logout => users.remove(req.id)

    case req: AuthRequest.CheckLogin =>
      sender ! new AuthResponse.CheckLogin(users.contains(req.id))

    case req: AuthRequest.GetStatus =>
      if (users.contains(req.id))
        sender ! new AuthResponse.GetStatus(users(req.id).status)
      else
        sender ! new AuthResponse.GetStatus(AuthStatus.ValueSet())

    case req: AuthRequest.SetStatus =>
      if (users.contains(req.id)) {
        users(req.id).status += req.status
        sender ! true
      } else {
        sender ! false
      }

    case req: AuthRequest.Migrate =>
      if (users.contains(req.userId) &&
        users(req.userId).status == (AuthStatus.LoggedIn + AuthStatus.PinAccepted + AuthStatus.PicAccepted)) {

        val key: Int = migrations.keys.reduceOption(_ max _).getOrElse(0) + 1
        migrations += key -> new MigrationHolder(req.charId, req.channel)

        sender ! new AuthResponse.Migrate(key)
      } else {
        sender ! new AuthResponse.Migrate(0)
      }

    case req: AuthRequest.GetMigration =>
      for (data <- migrations.get(req.key))
        sender ! new AuthResponse.GetMigration(data)
  }
}
