package com.github.wdonahoe.rpginventory

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptInput
import com.github.wdonahoe.rpginventory.service.ImportExportService
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService
import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import com.github.wdonahoe.rpginventory.util.FileUtil
import com.github.wdonahoe.rpginventory.view.Action
import com.github.wdonahoe.rpginventory.view.ProfileSelection
import com.github.wdonahoe.rpginventory.view.Prompt
import com.github.wdonahoe.rpginventory.view.Values.INDENT
import com.github.wdonahoe.rpginventory.view.Values.INVENTORY_SAMPLE
import com.github.wdonahoe.rpginventory.view.Values.RECIPES_SAMPLE
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

fun main(args: Array<String>) {
    if (args.none()) {
        startInteractiveMode()
    } else {
        handleArgs(args)
    }
}

fun handleArgs(args: Array<String>) {
    if (args.any { it.endsWith("zip", ignoreCase = true)}) {
        ImportProfile().main(args)
    } else {
        Profile()
            .subcommands(
                AddItem(),
                CraftItem(prompt),
                ListItems(prompt),
                Import()
            )
            .main(args)
    }
}

fun startInteractiveMode() {
    with(terminal) {
        println(prompt.welcome)

        if (profileManager.profiles.none()) {
            createNewProfile()
        }

        setInitialProfile()
        inventory = initializeInventory()

        do {
            val action = prompt.primaryActions

            when (action) {
                Action.SelectNewProfile -> selectOrCreateProfile()
                Action.AddItem          -> addItem()
                Action.CraftItem        -> craftItem()
                Action.AddRecipe        -> addRecipe()
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

    inventory = initializeInventory()
}

fun initializeInventory() =
    Inventory(
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

fun setInitialProfile() {
    if (profileManager.profiles.size > 1){
        val selection = prompt.selectProfile()
        when (selection.operation) {
            ProfileSelection.Operation.CreateNewProfile -> {
                createNewProfile()
            }
            ProfileSelection.Operation.ImportProfile -> {
                importProfile()
            }
            else -> {
                profileManager.setProfile(selection.profile)
            }
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
            Action.ImportItems   -> importItems()
            Action.ImportRecipes -> importRecipes()
            Action.ClearItems    -> clearItems()
            Action.ClearRecipes  -> clearRecipes()
            Action.ExportProfile -> exportProfile()
            else                 -> true
        }
    } while (action != Action.Back || !success)
}

fun importRecipes() =
    prompt.importRecipes().let { path ->
        if (path != null) {
            importExportService.importRecipes(path).let { (success, recipes) ->
                if (success) {
                    inventory.addRecipes(recipes)

                    terminal.println("recipes imported!".prependIndent(INDENT))
                } else {
                    terminal.print("failed to import recipes".prependIndent(INDENT))
                }

                success
            }

            true
        } else {
            displaySampleFile(RECIPES_SAMPLE)

            false
        }
    }

fun importItems() =
    prompt.importItems().let { path ->
        if (path != null) {
            importExportService.importItems(path).let { (success, items) ->
                if (success) {
                    inventory.addItems(items)

                    terminal.println("items imported!".prependIndent(INDENT))
                } else {
                    terminal.print("failed to import items".prependIndent(INDENT))
                }

                success
            }
        } else {
            displaySampleFile(INVENTORY_SAMPLE)

            false
        }
    }

fun displaySampleFile(file: String) =
    terminal.println(StringBuilder().apply {
        appendLine()
        appendLine((TextColors.brightWhite) (file))
        for (i in 0..7) {
            appendLine((TextColors.blue) ("~"))
        }
    }.toString())

fun clearItems() =
    inventory.clearItems().let { (success, message) ->
        terminal.println(if (success) {
            "inventory cleared!"
        } else {
            message
        }?.prependIndent(INDENT))

        success
    }

fun clearRecipes() =
    inventory.clearRecipes().let { (success, message) ->
        terminal.println(if (success) {
            "recipes cleared!"
        } else {
            message
        }?.prependIndent(INDENT))

        success
    }

fun exportProfile() : Boolean {
    var exportPath = prompt.exportProfile()

    if (exportPath.isBlank()) {
        exportPath = System.getProperty("user.home")
    }

    val (success, message) = importExportService.export(exportPath)

    terminal.println(message)

    return success
}

fun importProfile() : Boolean {
    val importPath = prompt.importProfile()

    if (importPath != null) {
        val (success, message) = profileManager.importProfile(importPath)

        if (success) {
            inventory = initializeInventory()

            terminal.println("profile \"${profileManager.currentProfile.name}\" imported!")
        } else {
            terminal.println("import failed: $message")
        }

        return success
    }

    return false
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