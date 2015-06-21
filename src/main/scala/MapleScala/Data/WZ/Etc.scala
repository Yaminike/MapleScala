package MapleScala.Data.WZ

import java.util.zip.GZIPInputStream

import scala.xml._

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
  var allowedEquips: Seq[Int] = null
  var forbiddenNames: Seq[String] = null

  def load(): Unit = {
    val file = getClass.getResourceAsStream("/XML/etc.xml.gz")
    val stream = new GZIPInputStream(file)
    val xml = XML.load(stream)

    allowedEquips = xml
      .\\("wzimg")
      .filter(_ \ "@name" exists (_.text == "MakeCharInfo.img"))
      .\("imgdir")
      .filter(_ \ "@name" exists (_.text == "Info"))
      .\("imgdir")
      .\("imgdir")
      .filter(_ \ "@name" exists (_.text.toInt > 4))
      .\("int")
      .map(x => (x \@ "value").toInt)
      .distinct

    forbiddenNames = xml
      .\\("wzimg")
      .filter(_ \ "@name" exists (_.text == "ForbiddenName.img"))
      .\("string")
      .map(x => (x \@ "value").toLowerCase)
  }
}
