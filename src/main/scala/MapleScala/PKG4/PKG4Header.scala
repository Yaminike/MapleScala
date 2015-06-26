package MapleScala.PKG4

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
final class PKG4Header(reader: PKG4Reader) {
  reader.seek(0)

  // Header validation
  val header = reader.getString(4)
  if (!header.equals("PKG4"))
    throw new RuntimeException(s"Invalid header, found '$header'  expected 'PKG4'")

  val nodeCount = reader.getUInt
  val nodeOffset = reader.getLong
  val stringCount = reader.getUInt
  val stringOffset = reader.getLong
  val bitmapCount = reader.getUInt
  val bitmapOffset = reader.getLong
  val soundCount = reader.getUInt
  val soundOffset = reader.getLong
}
