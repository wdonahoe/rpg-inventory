package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.service.File
import java.io.Reader
import java.io.Writer

internal object TestUtil {

    internal class ThrowawayFile : File {
        override fun getWriter(append: Boolean) = object : Writer() {
            override fun close() {
            }
            override fun flush() {
            }
            override fun write(cbuf: CharArray, off: Int, len: Int) {
            }
        }
        override val reader = object : Reader() {
            override fun read(cbuf: CharArray, off: Int, len: Int) = -1
            override fun close() {
            }
        }
    }

    internal val throwawayFile = ThrowawayFile()
}