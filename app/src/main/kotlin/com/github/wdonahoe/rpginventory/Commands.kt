package com.github.wdonahoe.rpginventory

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.service.ImportExportService
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService
import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import com.github.wdonahoe.rpginventory.util.FileUtil
import com.github.wdonahoe.rpginventory.view.Prompt
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM
import com.github.wdonahoe.rpginventory.view.Values.CRAFT_ITEM
import com.github.wdonahoe.rpginventory.view.Values.LIST_ITEMS
import java.io.IOException

class Profile: CliktCommand() {
    val profile by argument()

    override fun run() {
        val profileManager = ProfileManager(
            TableOfContentsFileService(
                FileUtil.getTableOfContentsFile(),
                profileCreatedCallback = FileUtil::getProfileDataFolder
            )
        ).apply {
            setProfile(profile)
        }

        val inventory = Inventory(
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

        currentContext.findOrSetObject {
            CommandContext(
                profileManager,
                inventory
            )
        }
    }
}

class AddItem: CliktCommand(ADD_ITEM, name = "add") {
    private val context by requireObject<CommandContext>()

    private val inventory get() =
        context.inventory

    val name by argument()
    val quantity by option("--quantity", "-q")
    val unit by option("--unit", "-u")

    override fun run() =
        inventory.addItem(Item(name, quantity?.toDoubleOrNull() ?: 1.0, unit)) // TODO use existing unit
}

class CraftItem(private val prompt: Prompt): CliktCommand(CRAFT_ITEM, name = "craft") {
    private val context by requireObject<CommandContext>()

    private val inventory get() =
        context.inventory

    val name by argument()

    override fun run() {
        inventory.recipes.firstOrNull { it.recipe.itemName.equals(name, ignoreCase = true) }.let { recipeStatus ->
            if (recipeStatus != null) {
                if (recipeStatus.canCraft) {
                    inventory.craftRecipe(recipeStatus.recipe)
                } else {
                    echo(prompt.displayRecipeDiff(recipeStatus))
                }
            } else {
                echo("A recipe for that item doesn't exist")
            }
        }
    }
}

class Import : CliktCommand("import items and recipes", name = "import") {
    private val context by requireObject<CommandContext>()

    private val profileManager get() =
        context.profileManager

    private val inventory get() =
        context.inventory
    
    val file by argument().file(mustExist = true)

    override fun run() {
        when {
            file.extension.equals("csv", ignoreCase = true) -> importItems()
            file.extension.equals("json", ignoreCase = true) -> importRecipes()
            else -> echo("please provide a csv file containing items or a json file containing recipes")
        }
    }

    private fun importItems() {
        try {
            ImportExportService(
                profileManager,
                inventory.inventoryService,
                inventory.recipeService
            ).apply {
                val (success, items) = importItems(file.absolutePath)
                if (success) {
                    inventory.addItems(items)

                    echo("items imported!")
                }
                else {
                    echo("failed to importProfile items!")
                }
            }
        } catch (ex: IOException) {
            echo(ex.message)
        }
    }

    private fun importRecipes() {
        try {
            ImportExportService(
                profileManager,
                inventory.inventoryService,
                inventory.recipeService
            ).apply {
                importRecipes(file.absolutePath).let { (success, recipes) ->
                    if (success) {
                        inventory.addRecipes(recipes)

                        echo("recipes imported!")
                    } else {
                        echo("failed to importProfile recipes!")
                    }
                }
            }
        } catch (ex: IOException) {
            echo(ex.message)
        }
    }
}

class ListItems(private val prompt: Prompt): CliktCommand(LIST_ITEMS, name = "list") {
    private val context by requireObject<CommandContext>()

    override fun run() =
        echo(prompt.displayItems(context.inventory.items))
}

class ImportProfile: CliktCommand("import a profile (.zip)") {
    val zipFile by argument().file(mustExist = true)

    override fun run() {
        ProfileManager(
            TableOfContentsFileService(
                FileUtil.getTableOfContentsFile(),
                profileCreatedCallback = FileUtil::getProfileDataFolder
            )
        ).apply {
            importProfile(zipFile.absolutePath).let { (success, message) ->
                if (success) {
                    echo("success")
                } else {
                    echo("fail")
                }
            }
        }
    }
}

data class CommandContext(
    val profileManager: ProfileManager,
    val inventory: Inventory
)