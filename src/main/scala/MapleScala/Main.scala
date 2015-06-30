package MapleScala

import MapleScala.Authorization.AuthHandler
import MapleScala.Connection.Server
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
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
object Main extends App {
  final lazy val conf = ConfigFactory.load()

  override def main(args: Array[String]): Unit = {
    initDB()
    println("Finished initializing database")

    Helper.time(loadWzData(), "Finished loading WZ data")

    val system = ActorSystem("MapleScala")
    val auth = system.actorOf(AuthHandler.create, "server-auth")
    system.actorOf(Server.create(conf.getInt("server.ports.login"), auth), "server-login")
  }

  def initDB() = {
    Class.forName(conf.getString("db.driver"))

    val settings = ConnectionPoolSettings(
      initialSize = conf.getInt("db.poolInitialSize"),
      maxSize = conf.getInt("db.poolMaxSize"),
      connectionTimeoutMillis = conf.getLong("db.poolConnectionTimeoutMillis"),
      validationQuery = conf.getString("db.poolValidationQuery"),
      connectionPoolFactoryName = conf.getString("db.poolFactoryName")
    )

    ConnectionPool.singleton(conf.getString("db.url"), conf.getString("db.user"), conf.getString("db.password"), settings)
  }

  def loadWzData(): Unit = {
    Data.WZ.Etc.load()
    Data.WZ.Character.load()
  }
}

