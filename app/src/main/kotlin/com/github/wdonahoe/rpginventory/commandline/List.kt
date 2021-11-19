package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@ExperimentalCli
class List  : Subcommand("list", "List the items in the inventoryFile.") {
    override fun execute() { }
}