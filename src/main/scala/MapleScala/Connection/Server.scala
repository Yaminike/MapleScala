package MapleScala.Connection

import java.net.InetSocketAddress

import akka.actor.SupervisorStrategy._
import akka.actor._
import akka.io._
import akka.routing.RoundRobinPool

import scala.concurrent.duration._

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
object Server {
  def create(port: Int, auth: ActorRef): Props = Props(new Server(port, auth))
}

class Server(port: Int, auth: ActorRef) extends Actor {

  import Tcp._
  import context.system

  // TODO: Custom error logging
  override val supervisorStrategy =
    OneForOneStrategy(loggingEnabled = true, maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _ => Restart // Try to keep it alive
    }

  val clientStrategy =
    OneForOneStrategy(loggingEnabled = true) {
      case _ => Stop // Always blame the client
    }

  IO(Tcp) ! Bind(self, new InetSocketAddress(MapleScala.Main.conf.getString("server.ip"), port))

  override def receive = {
    case Bound(localAddress) =>
      println(s"Server is running on ${localAddress.getPort}")
    case CommandFailed(_: Bind) =>
      println("Server failed to start")
      context.stop(self)
    case Connected(remote, local) =>
      val client = context.actorOf(
        RoundRobinPool(1, supervisorStrategy = clientStrategy)
          .props(Client.create(sender(), auth))
      )
      sender() ! Register(client)
      client ! new Client.Handshake
  }
}
