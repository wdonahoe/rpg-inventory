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
                BufferedWriter(inventory.getWriter(append = false)),
                CSVFormat.DEFAULT
            )

    fun readAll() =
        BufferedReader(inventory.reader).use { reader ->
            CSVParser(
                reader,
                CSVFormat.DEFAULT
            ).use { parser ->
                parser.map { record ->
                    Item(
                        name = record.get(0),
                        quantity = record.get(1).toDouble(),
                        unit = record.get(2)
                    )
                }
            }
        }

    fun clearInventory() {
        //Files.newBufferedWriter(inventory.reader, StandardOpenOption.TRUNCATE_EXISTING).use {  }
    }

    fun writeItems(items: List<Item>) {
        csvPrinter.use { printer ->
            items.forEach { item ->
                printer.printRecord(item.name, item.quantity, item.unit)
            }

            printer.flush()
        }
    }
}