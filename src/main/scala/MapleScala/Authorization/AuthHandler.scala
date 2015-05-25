package MapleScala.Authorization

import MapleScala.Data.User
import akka.actor.{Props, Actor}

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
  def receive = {
    case request: AuthRequest =>
      val user: User = User.getByName(request.username)
      val response = new AuthResponse(0, user)
      if (user == null)
        response.result = 5
      else if (!user.validatePassword(request.password))
        response.result = 4

      sender() ! response
  }
}
