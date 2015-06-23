package MapleScala.Data.WZ

import java.nio.ByteBuffer

import MapleScala.Helper._
import PKGNX.LazyNXFile
import PKGNX.Nodes.{NXLongNode, NXStringNode}

import scala.collection.mutable._

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
  var allowedEquips: MutableList[Long] = new MutableList[Long]()
  var forbiddenNames: MutableList[String] = new MutableList[String]()

  def load(): Unit = {
    var bytes: ByteBuffer = null

    using(getClass.getResourceAsStream("/XML/Etc.nx.gz"))(stream => {
      bytes = GzipParser.readGzip(stream)
    })

    val reader = new LazyNXFile(bytes)
    for (node <- reader.resolve("MakeCharInfo.img/Info")) {
      for (subNode <- node) {
        // CharFemale | CharMale
        for (valueNode <- subNode) {
          val value = valueNode.asInstanceOf[NXLongNode].get()
          if (value > 1e6)
            allowedEquips += value
        }
      }
    }

    for (node <- reader.resolve("ForbiddenName.img")) {
      forbiddenNames += node.asInstanceOf[NXStringNode].get()
    }
  }
}
