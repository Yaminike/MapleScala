package MapleScala.Authorization

import MapleScala.Data.User
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
  val users: mutable.Map[Int, AuthHolder] = mutable.Map()

  def receive = {
    case req: AuthRequest.Login =>
      val user: Option[User] = User.getByName(req.username)
      if (user.isEmpty) {
        sender ! new AuthResponse.Login(5, user)
      } else if (!user.get.validatePassword(req.password)) {
        sender ! new AuthResponse.Login(4, user)
      } else {
        users += user.get.id -> new AuthHolder {
          status += AuthStatus.LoggedIn
        }
        sender ! new AuthResponse.Login(0, user)
      }

    case req: AuthRequest.Logout =>
      if (users.contains(req.id))
        users.remove(req.id)

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
  }
}
