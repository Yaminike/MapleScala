package MapleScala.Data.WZ

import java.nio.ByteBuffer

import MapleScala.Helper._
import MapleScala.PKG4._

import scala.collection.mutable
import scala.util.Try

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
object Character {
  val itemInfo = new mutable.HashMap[Int, Map[String, Long]]()

  def load(): Unit = {
    var bytes: ByteBuffer = null

    using(getClass.getResourceAsStream("/XML/Character.nx.gz"))(stream => {
      bytes = GzipParser.readGzip(stream)
    })

    val reader = new PKG4Reader(bytes)
    for {
      root <- reader.resolve("/")
      typeNode <- root.filterNot(x => x.name.endsWith(".img"))
      itemNode <- typeNode
      itemId <- Try(itemNode.name.stripSuffix(".img").toInt)
      infoNode <- itemNode.getChild("info")
    } {
      itemInfo += itemId -> infoNode
        .filter(x => x.name.startsWith("inc") ||
        (x.name match {
          case "reqJob" | "reqLevel" | "reqDEX" | "reqSTR" | "reqINT" | "reqLUK" | "reqPOP" => true
          case "cash" | "tuc" | "cursed" | "success" | "fs" | "tradeBlock" | "quest" => true
          case _ => false
        })
        )
        .filter(x => x.value.isInstanceOf[Long])
        .map(x => x.name -> x.value.asInstanceOf[Long])
        .toMap
    }
  }
}
