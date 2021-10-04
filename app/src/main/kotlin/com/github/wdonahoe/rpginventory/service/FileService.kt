package com.github.wdonahoe.rpginventory.service

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.Closeable

abstract class FileService(protected val file: File) : Closeable {

    private val writer by lazy {
        BufferedWriter(file.writer)
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
}