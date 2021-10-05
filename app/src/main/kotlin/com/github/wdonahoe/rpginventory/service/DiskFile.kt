package com.github.wdonahoe.rpginventory.service

import java.io.*

class DiskFile(private val file: java.io.File) : File {
    override val writer get() = FileWriter(file, true)
    override val reader get() = FileReader(file)
}