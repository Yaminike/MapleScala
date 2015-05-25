package MapleScala.Connection

import java.net.InetSocketAddress

import MapleScala.Authorization.AuthRequest
import akka.actor._
import akka.io._
import akka.pattern.pipe

import scala.concurrent.Future

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

  IO(Tcp) ! Bind(self, new InetSocketAddress(MapleScala.Main.conf.getString("server.ip"), port))

  override def receive = {
    case ar: AuthRequest =>
      auth.forward(ar)
    case Bound(localAddress) =>
      println(s"Server is running on ${localAddress.getPort}")
    case CommandFailed(_: Bind) =>
      println("Server failed to start")
      context.stop(self)
    case Connected(remote, local) =>
      val handler = context.actorOf(Client.create(sender(), self))
      sender() ! Register(handler)
  }
}
