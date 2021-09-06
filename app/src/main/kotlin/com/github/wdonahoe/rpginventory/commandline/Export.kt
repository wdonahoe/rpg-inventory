package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.required

class Export : Subcommand("export", "Export the inventory to the specified location.") {
    val path by option(ArgType.String, "out", "o").required()

    override fun execute() { }
}