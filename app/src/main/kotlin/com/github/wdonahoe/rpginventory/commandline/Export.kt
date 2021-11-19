package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.required

@ExperimentalCli
class Export : Subcommand("export", "Export the inventoryFile to the specified location.") {
    val path by option(ArgType.String, "out", "o").required()

    override fun execute() { }
}