package com.github.wdonahoe.rpginventory

import com.github.ajalt.mordant.terminal.Terminal
import com.github.wdonahoe.rpginventory.commandline.*
import com.github.wdonahoe.rpginventory.commandline.List
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.ProfileFileService
import com.github.wdonahoe.rpginventory.view.ProfileSelection
import com.github.wdonahoe.rpginventory.view.Prompt
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import java.text.DecimalFormat

private lateinit var inventory : Inventory

private val profileService by lazy {
    ProfileService(ProfileFileService())
}

private val prompt by lazy {
    Prompt(profileService)
}

@ExperimentalCli
fun main(args: Array<String>) {
    if (args.none()) {
        startInteractiveMode()
    } else {
        handleArgs(args)
    }
}

fun startInteractiveMode() {
    with(Terminal()) {
        println(prompt.welcome)

        if (profileService.profiles.none()) {
            createNewProfile()
        }

        if (profileService.profiles.size > 1){
            val selection = prompt.selectProfile
            if (selection.operation == ProfileSelection.Operation.CreateNewProfile) {
                createNewProfile()
            } else {
                profileService.setProfile(selection.profile)
            }
        }

        inventory = Inventory(InventoryFileService(profileService))
    }
}

fun createNewProfile() {
    var profile = ""
    while (profile.isBlank()) {
        println(prompt.noExistingProfileCreate)

        readLine()?.also { read ->
            profile = read

            profileService.setProfile(profile)
        }
    }
}

@ExperimentalCli
fun handleArgs(args: Array<String>) {
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
        //export.name -> inventory.export(export.path).also { exportStatus(export.path, it) }
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