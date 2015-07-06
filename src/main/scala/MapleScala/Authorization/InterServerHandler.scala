package MapleScala.Authorization

import MapleScala.Data.User
import MapleScala.Main
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
object InterServerHandler {
  def create: Props = Props(new InterServerHandler())
}

class InterServerHandler extends Actor {
  val states: mutable.HashMap[Int, AuthHolder] = mutable.HashMap()
  val migrations: mutable.HashMap[Int, Int] = mutable.HashMap()

  def receive = {
    case req: AuthRequest.Login =>
      val userOption = User.getByName(req.username)
      val response = new AuthResponse.Login(6, userOption)
      userOption match {
        case Some(user) =>
          if (!user.validatePassword(req.password)) {
            response.result = 4
          } else {
            val holder = new AuthHolder()
            holder.status += AuthStatus.LoggedIn
            if (!Main.pinEnabled)
              holder.status += AuthStatus.PinAccepted
            if (!Main.picEnabled)
              holder.status += AuthStatus.PicAccepted

            states += user.id -> holder
            response.result = 0
          }
        case None => response.result = 5
      }
      sender ! response

    case req: AuthRequest.Logout =>
      states.remove(req.id)
      migrations.remove(req.id)

    case req: AuthRequest.CheckLogin =>
      sender ! new AuthResponse.CheckLogin(states.contains(req.id))

    case req: AuthRequest.GetStatus =>
      sender ! {
        states.get(req.id) match {
          case Some(holder) => new AuthResponse.GetStatus(states(req.id))
          case None => None
        }
      }

    case req: AuthRequest.SetStatus =>
      sender ! {
        states.get(req.id) match {
          case Some(holder) =>
            holder.status += req.status
            states(req.id) = holder
            Some
          case None => None
        }
      }

    case req: AuthRequest.CreateMigration =>
      sender ! {
        states.get(req.userId) match {
          case Some(holder) =>
            holder.characterId = req.characterId
            holder.channel = req.channel
            states(req.userId) = holder

            if (holder.status == AuthStatus.All) {
              holder.status = AuthStatus.ValueSet()

              val key = generateMigrationKey()
              migrations(req.userId) = key
              key
            } else {
              // TODO: only allow to get this when no other client with such user id is online
              migrations.getOrElse(req.userId, None)
            }
          case None => None
        }
      }

    case req: AuthRequest.GetMigration =>
      sender ! {
        migrations.find(_._2 == req.key) match {
          case Some(value) => states.get(value._1) match {
            case Some(holder) => new AuthResponse.GetMigration(holder)
            case None => None
          }
          case None => None
        }
      }
  }

  private def generateMigrationKey(): Int = {
    val key: Int = MapleScala.Helper.random.nextInt()
    migrations.get(key) match {
      case Some(value) => generateMigrationKey()
      case None => key
    }
  }
}
