package com.github.wdonahoe.rpginventory.service

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import java.io.*

abstract class FileService : Closeable {

    protected abstract val file : File

    private val writer by lazy {
        BufferedWriter(FileWriter(file, true))
    }

    protected val printer by lazy {
        CSVPrinter(
            writer,
            CSVFormat.DEFAULT
        )
    }

    override fun close() {
        printer.close()
        writer.close()
    }

    companion object {
        private const val DATA_DIR = "rpginventory"

        val rootDir by lazy {
            (if (SystemUtils.IS_OS_WINDOWS) {
                File(FilenameUtils.concat(System.getenv("APPDATA"), DATA_DIR))
            } else {
                File(FilenameUtils.concat(SystemUtils.USER_HOME, ".$DATA_DIR"))
            }).apply {
                mkdir()
            }
        }
    }
}