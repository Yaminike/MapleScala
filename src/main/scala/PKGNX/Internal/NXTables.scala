package PKGNX.Internal

import java.awt.image.BufferedImage

import PKGNX.NXException
import PKGNX.Util.BufferedReader

/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Aaron Weiss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * A set of data tables bound to a NXFile.
 */
abstract class NXTables {
  /**
   * Looks up a sequence of audio data from the audio table.
   *
   * @param index  the starting index of the audio data
   * @param length the length of the audio data
   * @return the audio data as a ByteBuffer
   */
  def getAudioBuf(index: Long, length: Long): BufferedReader


  /**
   * Looks up a bitmap image from the bitmap table.
   *
   * @param index  the index of the bitmap
   * @param width  the width of the image
   * @param height the height of the image
   * @return the bitmap as a BufferedImage
   */
  def getImage(index: Long, width: Int, height: Int): BufferedImage

  /**
   * Looks up a string from the string table.
   *
   * @param index the index of the string
   * @return the string
   */
  def getString(index: Long): String

  /**
   * Checks if the offset index is legal.
   *
   * @param index the index to check
   * @throws NXException if the offset index is not legal
   */
  protected def checkIndex(index: Long): Unit = {
    if (index > Int.MaxValue)
      throw new NXException("Index overflow")
  }
}
