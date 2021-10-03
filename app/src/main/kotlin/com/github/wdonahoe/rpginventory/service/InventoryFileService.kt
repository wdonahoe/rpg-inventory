package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.ProfileService
import com.github.wdonahoe.rpginventory.model.Item
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class InventoryFileService(private val profileService: ProfileService) : FileService() {

    override val file =
        File(getProfileDir(), INVENTORY_CSV).apply {
            createNewFile()
        }

    private val inventory = file

    private fun getProfileDir(): String =
        FilenameUtils.concat(rootDir.absolutePath, profileService.currentProfile.folder)

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

    fun clearInventory() {
        Files.newBufferedWriter(inventory.toPath(), StandardOpenOption.TRUNCATE_EXISTING).use {  }
    }

    fun writeToInventory(item: Item) {
        printer.printRecord(item.name, item.quantity, item.unit)
        printer.flush()
    }

    companion object {
        private const val INVENTORY_CSV = "inventory.csv"
    }
}