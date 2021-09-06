package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.commandline.*
import com.github.wdonahoe.rpginventory.commandline.List
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import java.text.DecimalFormat

val inventory : Inventory by lazy {
    Inventory(FileService())
}

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("inventory", strictSubcommandOptionsOrder = true)

    val add = Add()
    val list = List()
    val clear = Clear()
    val export = Export()

    parser.subcommands(add, list, clear, export)

    when (parser.parse(args).commandName) {
        add.name -> add.result?.withUnit()?.also(inventory::addItem) ?: warn()
        list.name -> printInventory()
        clear.name -> inventory.clear().also(::clearStatus)
        export.name -> inventory.export(export.path).also { exportStatus(export.path, it) }
    }
}

fun clearStatus(cleared: Boolean) {
    if (cleared) {
        println("Inventory cleared!")
    } else {
        println("The inventory could not be cleared.")
    }
}

fun exportStatus(path: String, exported: Boolean) {
    if (exported) {
        println("Inventory exported to $path.")
    } else {
        println("The inventory could not be exported.")
    }
}

fun printInventory() {
    inventory.apply {
        if (items.none()) {
            println("There are no items in the inventory")
        }
        else {
            val leftAlignFormat = "| %-32s | %-8s |%n"

            System.out.format("+----------------------------------+----------+%n")
            System.out.format("| Item Name                        | Quantity |%n")
            System.out.format("+----------------------------------+----------+%n")

            for (item in items) {
                item.apply {
                    val isDecimal = quantity % 1 != 0.0

                    System.out.format(
                        leftAlignFormat,
                        name,
                        "${
                            if (isDecimal)
                                DecimalFormat("#.##").format(quantity)
                            else
                                "${quantity.toInt()}"
                        } ${unit ?: ""}")
                }
            }

            System.out.format("+----------------------------------+----------+%n")
        }
    }
}

fun warn() {
    println("warning: the item was not added")
}