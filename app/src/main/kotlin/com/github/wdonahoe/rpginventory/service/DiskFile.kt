package com.github.wdonahoe.rpginventory.service

import java.io.*

class DiskFile(private val file: java.io.File) : File {
    val path = file.absolutePath
    override fun getWriter(append: Boolean) = FileWriter(file, append)
    override val reader get() = FileReader(file)
}