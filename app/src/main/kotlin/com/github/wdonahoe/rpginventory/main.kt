package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.commandline.Add
import com.github.wdonahoe.rpginventory.commandline.withUnit
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

val inventory : Inventory by lazy {
    Inventory(FileService())
}

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("inventory", strictSubcommandOptionsOrder = true)

    val add = Add()

    parser.subcommands(add)

    when (parser.parse(args).commandName) {
        add.name -> add.result?.withUnit()?.also(inventory::addItem) ?: warn()
    }
}

fun warn() {
    println("warning: the item was not added")
}
