package com.github.wdonahoe.rpginventory.service

import java.io.*

class DiskFile(file: java.io.File) : File {
    override val writer = FileWriter(file, true)
    override val reader = FileReader(file)
}