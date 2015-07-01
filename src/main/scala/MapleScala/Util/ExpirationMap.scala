package MapleScala.Util

import java.util.Date

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
class ExpirationMap[A, B](expiryLength: Long) extends Map[A, B] {
  var keyValueExpiry: Map[A, (B, Long)] = Map()

  def currentTime: Long = new Date().getTime

  def get(key: A): Option[B] =
    keyValueExpiry.get(key) collect {
      case (value, expiry) if expiry > currentTime => value
    }

  def iterator: Iterator[(A, B)] = keyValueExpiry.iterator.filter(_._2._2 > currentTime).map(p => (p._1, p._2._1))

  def clearedExpired(): Unit =
    keyValueExpiry = keyValueExpiry.filter(_._2._2 < currentTime)

  def +[B1 >: B](kv: (A, B1)): ExpirationMap[A, B] = {
    clearedExpired()
    val item: (B, Long) = (kv._2.asInstanceOf[B], currentTime + expiryLength)
    keyValueExpiry += kv._1 -> item
    this
  }

  def -(key: A): ExpirationMap[A, B] = {
    keyValueExpiry -= key
    clearedExpired()
    this
  }
}