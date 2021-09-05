package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.commandline.Add
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

val inventory : Inventory by lazy {
    Inventory()
}

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("inventory", strictSubcommandOptionsOrder = true)

    val add = Add()

    parser.subcommands(add)

    when (parser.parse(args).commandName) {
        add.name -> inventory.addItem(add.result)
    }
}