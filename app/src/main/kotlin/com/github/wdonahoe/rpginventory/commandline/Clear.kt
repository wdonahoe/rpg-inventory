package com.github.wdonahoe.rpginventory.commandline

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@ExperimentalCli
class Clear : Subcommand("clear", "Clear the inventory") {
    override fun execute() { }
}