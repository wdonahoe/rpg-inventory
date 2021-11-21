package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@ExperimentalCli
class Clear : Subcommand("clearItems", "Clear the inventoryFile") {
    override fun execute() { }
}