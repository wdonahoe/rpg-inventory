package com.github.wdonahoe.rpginventory.commandline

import com.github.wdonahoe.rpginventory.model.Item
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default

@OptIn(ExperimentalCli::class)
class Add : Subcommand("add", "Add an item to the inventory") {

    val itemName by argument(ArgType.String)
    val quantity by option(ArgType.String, shortName = "q", fullName = "quantity").default("1")

    var result: Item? = null

    override fun execute() {
        result = Item(itemName, quantity)
    }
}