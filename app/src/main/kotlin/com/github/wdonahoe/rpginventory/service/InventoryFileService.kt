package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.model.Item
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedReader
import java.io.BufferedWriter

class InventoryFileService(private val inventory: File) {

    private val csvPrinter
        get() =
            CSVPrinter(
                BufferedWriter(inventory.writer),
                CSVFormat.DEFAULT
            )

    fun readAll() =
        BufferedReader(inventory.reader).use {
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
        //Files.newBufferedWriter(inventory.reader, StandardOpenOption.TRUNCATE_EXISTING).use {  }
    }

    fun writeToInventory(item: Item) {
        csvPrinter.use { printer ->
            printer.printRecord(item.name, item.quantity, item.unit)
            printer.flush()
        }
    }
}