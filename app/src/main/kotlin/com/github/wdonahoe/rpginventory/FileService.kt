package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import java.io.*
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class FileService : Closeable {

    private val inventory = if (SystemUtils.IS_OS_WINDOWS) {
        File(FilenameUtils.concat(System.getenv("APPDATA"), DATA_DIR), INVENTORY_CSV)
    } else {
        File(FilenameUtils.concat(SystemUtils.USER_HOME, ".${DATA_DIR}"), INVENTORY_CSV)
    }

    private val writer by lazy {
        BufferedWriter(FileWriter(inventory, true))
    }

    private val printer by lazy {
        CSVPrinter(
            writer,
            CSVFormat.DEFAULT
        )
    }

    init {
        if (!inventory.parentFile.exists()) {
            inventory.parentFile.mkdirs()
        }

        inventory.createNewFile()
    }

    fun readAll() =
        BufferedReader(FileReader(inventory)).use {
            CSVParser(
                it,
                CSVFormat.DEFAULT
            ).use { parser ->
                sequence {
                    for (record in parser) {
                        yield(
                            Item(
                                name = record.get(0),
                                quantity = record.get(1).toDouble(),
                                unit = record.get(2)
                            )
                        )
                    }
                }
            }
        }

    override fun close() {
        printer.close()
        writer.close()
    }

    fun clearInventory() {
        Files.newBufferedWriter(inventory.toPath(), StandardOpenOption.TRUNCATE_EXISTING).use {  }
    }

    fun writeToInventory(item: Item) {
        printer.printRecord(item.name, item.quantity, item.unit)
        printer.flush()
    }

    fun copyTo(path: String) =
        try {
            inventory.copyTo(File(path), overwrite = true)
            true
        } catch (ex: Exception) {
            false
        }

    companion object {
        const val INVENTORY_CSV = "inventory.csv"
        const val DATA_DIR = "rpginventory"
    }
}