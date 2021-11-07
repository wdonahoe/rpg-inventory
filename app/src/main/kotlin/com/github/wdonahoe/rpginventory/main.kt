package com.github.wdonahoe.rpginventory

import com.github.ajalt.mordant.terminal.Terminal
import com.github.wdonahoe.rpginventory.commandline.*
import com.github.wdonahoe.rpginventory.commandline.List
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import com.github.wdonahoe.rpginventory.util.FileUtil
import com.github.wdonahoe.rpginventory.view.Action
import com.github.wdonahoe.rpginventory.view.ProfileSelection
import com.github.wdonahoe.rpginventory.view.Prompt
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.core.KInquirer
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import java.text.DecimalFormat

private val terminal = Terminal()

private lateinit var inventory : Inventory

private val profileManager by lazy {
    ProfileManager(
        TableOfContentsFileService(
            FileUtil.getTableOfContentsFile(),
            profileCreatedCallback = FileUtil::getProfileDataFolder
        )
    )
}

private val prompt by lazy {
    Prompt(profileManager)
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
    with(terminal) {
        println(prompt.welcome)

        if (profileManager.profiles.none()) {
            createNewProfile()
        }

        setInitialProfile()
        initializeInventory()

        do {
            val action = prompt.primaryActions

            when (action) {
                Action.SelectNewProfile -> selectOrCreateProfile()
                Action.AddItem          -> addItem()
                Action.AddRecipe        -> addRecipe()
                Action.RemoveItem       -> removeItems()
                Action.ListItems        -> listItems()
                else -> { }
            }
        } while(action != Action.Exit)
    }
}

fun addRecipe() {
    val recipe = prompt.addRecipe()

    terminal.println(recipe.first)
    terminal.println(prompt.displayItems(recipe.second))
}

fun removeItems() {
    val toRemove = prompt.removeItems(inventory.items)

    inventory.removeAllItems(toRemove)
}

fun listItems() {
    terminal.println(prompt.displayItems(inventory.items))
}

fun addItem() {
    prompt.addItem().let { item ->
        terminal.println(prompt.displayItem(item))

        inventory.addItem(item)
    }
}

fun selectOrCreateProfile() {
    if (profileManager.profiles.size == 1) {
        createNewProfile()
    } else {
        setInitialProfile()
    }

    initializeInventory()
}

fun initializeInventory() {
    inventory = Inventory(
        InventoryFileService(
            FileUtil.getInventoryFile(
                profileManager.currentProfile
            )
        )
    )
}

fun setInitialProfile() {
    if (profileManager.profiles.size > 1){
        val selection = prompt.selectProfile
        if (selection.operation == ProfileSelection.Operation.CreateNewProfile) {
            createNewProfile()
        } else {
            profileManager.setProfile(selection.profile)
        }
    } else {
        profileManager.useFirstProfile()
    }
}

fun createNewProfile() {
    do {
        KInquirer.promptInput(prompt.noExistingProfileCreate).also { profile ->
            profileManager.setProfile(profile)
        }
    } while (!profileManager.isInitialized())
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