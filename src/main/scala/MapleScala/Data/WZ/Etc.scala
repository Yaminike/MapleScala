package MapleScala.Data.WZ

import java.nio.ByteBuffer

import MapleScala.Helper._
import MapleScala.PKG4._

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
object Etc {
  val allowedEquips = new mutable.HashSet[Long]()
  val forbiddenNames = new mutable.MutableList[String]()

  def load(): Unit = {
    var bytes: ByteBuffer = null

    using(getClass.getResourceAsStream("/XML/Etc.nx.gz"))(stream => {
      bytes = GzipParser.readGzip(stream)
    })

    val reader = new PKG4Reader(bytes)
    for (root <- reader.resolve("MakeCharInfo.img/Info"); node <- root; subNode <- node; valueNode <- subNode) {
      val value = valueNode.value.asInstanceOf[Long]
      if (value > 1e6)
        allowedEquips += value
    }

    for (root <- reader.resolve("ForbiddenName.img"); node <- root) {
      forbiddenNames += node.value.asInstanceOf[String].toLowerCase
    }
  }
}
