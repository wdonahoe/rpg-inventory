package com.github.wdonahoe.rpginventory

import com.github.ajalt.mordant.terminal.Terminal
import com.github.wdonahoe.rpginventory.commandline.*
import com.github.wdonahoe.rpginventory.commandline.List
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.service.ImportExportService
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService
import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import com.github.wdonahoe.rpginventory.util.FileUtil
import com.github.wdonahoe.rpginventory.view.Action
import com.github.wdonahoe.rpginventory.view.ProfileSelection
import com.github.wdonahoe.rpginventory.view.Prompt
import com.github.wdonahoe.rpginventory.view.Values.INDENT
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

private val importExportService get() =
    ImportExportService(
        profileManager,
        inventory.inventoryService,
        inventory.recipeService
    )

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
                Action.CraftItem        -> craftItem()
                Action.AddRecipe        -> addRecipe()
                Action.RemoveItem       -> removeItems()
                Action.ListItems        -> listItems()
                Action.Advanced         -> displayAdvanced()
                else -> { }
            }
        } while(action != Action.Exit)
    }
}

fun craftItem() {
    prompt.craftRecipe(inventory.recipes)?.let { recipeStatus ->
        if (recipeStatus.canCraft) {
            inventory.craftRecipe(recipeStatus.recipe)
        } else {
            terminal.print(prompt.displayRecipeDiff(recipeStatus))
        }
    }
}

fun addRecipe() {
    prompt.addRecipe(inventory).let { recipe ->
        inventory.addRecipe(recipe)

        terminal.println(recipe.itemName)
        terminal.println(prompt.displayItems(recipe.ingredients))
    }
}

fun removeItems() {
    val toRemove = prompt.removeItems(inventory.items)

    inventory.removeAllItems(toRemove)
}

fun listItems() {
    terminal.println(prompt.displayItems(inventory.items))
}

fun addItem() {
    prompt.addItem(inventory).let { item ->
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
        ),
        RecipeFileService(
            FileUtil.getRecipeFile(
                profileManager.currentProfile
            )
        )
    )
}

fun setInitialProfile() {
    if (profileManager.profiles.size > 1){
        val selection = prompt.selectProfile()
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

fun displayAdvanced() {
    do {
        val action = prompt.advancedActions

        val success = when (action) {
            Action.ImportProfile -> importProfile()
            Action.ExportProfile -> exportProfile()
            Action.ClearItems    -> clearItems()
            else                 -> true
        }
    } while (action != Action.Back || !success)
}

fun clearItems() =
    inventory.clear().let { (success, message) ->
        terminal.println(if (success) {
            "inventory cleared!"
        } else {
            message
        }?.prependIndent(INDENT))

        success
    }

fun exportProfile() : Boolean {
    val exportPath = prompt.exportProfile()

    return if (exportPath != null) {
        val (success, message) = importExportService.export(exportPath)

        terminal.println(message)

        success
    } else {
        false
    }
}

fun importProfile() : Boolean {
    return true
//    val importPath = prompt.importProfile()
//
//    return if (importPath != null) {
//        val (success, message, profile) = importExportService.import(importPath)
//
//        terminal.println(message)
//
//        if (success) {
//            profileManager.setProfile(profile)
//        }
//
//        success
//    } else {
//        false
//    }
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
        //clear.name -> inventory.clear().also(::clearStatus)
        //export.name -> inventoryFile.export(export.path).also { exportStatus(export.path, it) }
    }
}

fun clearStatus(cleared: Boolean) {
    if (cleared) {
        println("Inventory cleared!")
    } else {
        println("The inventoryFile could not be cleared.")
    }
}

fun printInventory() {
    inventory.apply {
        if (items.none()) {
            println("There are no items in the inventoryFile")
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