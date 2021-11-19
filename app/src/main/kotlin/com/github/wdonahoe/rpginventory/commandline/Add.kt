package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.default

@ExperimentalCli
class Add : Subcommand("add", "Add an item to the inventoryFile") {

    private val itemName by argument(ArgType.String)
    private val quantity by option(ArgType.String, shortName = "q", fullName = "quantity").default("1")
    private val unit by option(ArgType.String, shortName = "u", fullName = "unit").default("")

    var result: Item? = null

    override fun execute() {
        result = Item(itemName, quantity, unit)
    }
}