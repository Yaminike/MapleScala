package MapleScala

import scala.util.Random

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
object Helper {
  def random = new Random()

  def toHex(bytes: Array[Byte], sep: String = " "): String = {
    bytes.map("%02X".format(_)).mkString(sep)
  }

  def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

  def time[R](block: => R, message: String): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val passed = (System.nanoTime() - t0) / 1000000000.0
    println(s"$message in $passed seconds")
    result
  }
}
