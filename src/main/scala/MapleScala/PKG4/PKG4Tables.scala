package MapleScala.PKG4

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
final class PKG4Tables(reader: PKG4Reader) {
  val strings = new mutable.HashMap[Long, String]

  def getString(index: Long): String =
    strings.getOrElse(index, {
      reader.mark()
      reader.seek(reader.header.stringOffset + index * 8)
      reader.seek(reader.getLong)
      strings.put(index, reader.getString)
      reader.reset()
      strings(index)
    })
}
