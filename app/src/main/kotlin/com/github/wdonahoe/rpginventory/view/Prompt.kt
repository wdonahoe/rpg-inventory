package com.github.wdonahoe.rpginventory.view

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.wdonahoe.rpginventory.Inventory
import com.github.wdonahoe.rpginventory.ProfileManager
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.model.RecipeStatus
import com.github.wdonahoe.rpginventory.view.Values.ACTIONS_HEADER
import com.github.wdonahoe.rpginventory.view.Values.ADD_ADDITIONAL_INGREDIENT
import com.github.wdonahoe.rpginventory.view.Values.ADD_INGREDIENT
import com.github.wdonahoe.rpginventory.view.Values.ADD_INITIAL_INGREDIENT
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_ENTER_UNITS
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_HEADER
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_HINT
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_QUANTITY
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_UNITS
import com.github.wdonahoe.rpginventory.view.Values.ADD_RECIPE
import com.github.wdonahoe.rpginventory.view.Values.ADD_RECIPE_HEADER
import com.github.wdonahoe.rpginventory.view.Values.ADVANCED
import com.github.wdonahoe.rpginventory.view.Values.BACK
import com.github.wdonahoe.rpginventory.view.Values.CANCEL
import com.github.wdonahoe.rpginventory.view.Values.CLEAR_INVENTORY
import com.github.wdonahoe.rpginventory.view.Values.CLEAR_RECIPES
import com.github.wdonahoe.rpginventory.view.Values.CRAFT_ITEM
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_OPTION
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.EXIT
import com.github.wdonahoe.rpginventory.view.Values.EXPORT_PATH
import com.github.wdonahoe.rpginventory.view.Values.EXPORT_PROFILE
import com.github.wdonahoe.rpginventory.view.Values.FINISH_RECIPE
import com.github.wdonahoe.rpginventory.view.Values.IMPORT_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.IMPORT_PROFILE
import com.github.wdonahoe.rpginventory.view.Values.IMPORT_RECIPES
import com.github.wdonahoe.rpginventory.view.Values.INDENT
import com.github.wdonahoe.rpginventory.view.Values.LIST_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.REMOVE_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.SELECT_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.SWITCH_PROFILE
import com.github.wdonahoe.rpginventory.view.Values.TABLE_PADDING
import com.github.wdonahoe.rpginventory.view.Values.WELCOME
import com.jakewharton.picnic.TableSectionDsl
import com.jakewharton.picnic.table
import com.yg.kotlin.inquirer.components.*
import com.yg.kotlin.inquirer.core.KInquirer

class Prompt(private val profileManager: ProfileManager) {

    val welcome by lazy {
        (brightYellow + bold)(WELCOME)
    }

    val noExistingProfileCreate by lazy {
        (brightWhite + bold)(CREATE_PROFILE_PROMPT)
    }

    fun selectProfile() =
        KInquirer.promptList(
            SELECT_PROFILE_PROMPT.apply {
                if (profileManager.isInitialized()) {
                    prependProfile()
                }
            },
            profileManager.profiles.mapIndexed { index, profile ->
                " ${index + 1}) ${profile.name}"
            }.plus(
                CREATE_PROFILE_OPTION
            )
        ).run {
            when(this) {
                CREATE_PROFILE_OPTION -> ProfileSelection(ProfileSelection.Operation.CreateNewProfile)
                else -> ProfileSelection(ProfileSelection.Operation.SelectProfile, split(")")[1].trim())
            }
        }

    private fun promptActions(message: String, map: List<Pair<String, Action>>) =
        KInquirer.promptList(
            message,
            map.mapIndexed { index, (msg, _) ->
                " ${index + 1}) $msg"
            }
        ).run {
            split(")")
                .first()
                .trim()
                .toInt()
                .let { index ->
                    map[index - 1].second
                }
        }

    val primaryActions get() =
        promptActions(
            ACTIONS_HEADER.prependProfile(),
            listOf(
                ADD_ITEM        to Action.AddItem,
                ADD_RECIPE      to Action.AddRecipe,
                CRAFT_ITEM      to Action.CraftItem,
                REMOVE_ITEMS    to Action.RemoveItem,
                LIST_ITEMS      to Action.ListItems,
                SWITCH_PROFILE  to Action.SelectNewProfile,
                ADVANCED        to Action.Advanced,
                EXIT            to Action.Exit
            )
        )

    val advancedActions get() =
        promptActions(
            ACTIONS_HEADER.prependProfile(),
            listOf(
                IMPORT_ITEMS    to Action.ImportItems,
                IMPORT_RECIPES  to Action.ImportRecipes,
                IMPORT_PROFILE  to Action.ImportProfile,
                CLEAR_INVENTORY to Action.ClearItems,
                CLEAR_RECIPES   to Action.ClearRecipes,
                EXPORT_PROFILE  to Action.ExportProfile,
                BACK            to Action.Back
            )
        )

    private fun addItemName(prompt: String) =
        KInquirer.promptInput(
            prompt.prependProfile(),
            validation = String::isNotBlank,
            hint = ADD_ITEM_HINT
        ).trim()

    private fun addItemHasUnit() =
        KInquirer.promptConfirm(
            ADD_ITEM_UNITS.prependProfile(),
            default = false
        )

    private fun addItemUnit() =
        KInquirer.promptInput(
            ADD_ITEM_ENTER_UNITS.prependProfile(),
        )

    private fun addItemQuantity(unit: String? = null) =
        KInquirer.promptInputNumber(
            "$ADD_ITEM_QUANTITY${if (unit != null) " ($unit)" else ""}?".prependProfile()
        )

    fun addItem(inventory: Inventory, prompt: String = ADD_ITEM_HEADER) =
        addItemName(prompt).let { itemName ->
            val existingUnit = inventory.getUnit(itemName)

            (if (existingUnit != null) true else addItemHasUnit()).let { hasUnit ->
                if (hasUnit) {
                    val unit = existingUnit ?: addItemUnit()

                    Item(itemName, addItemQuantity(unit).toDouble(), unit)
                } else {
                    Item(itemName, addItemQuantity().toDouble(), "")
                }
            }
        }

    private fun addRecipeName() =
        KInquirer.promptInput(
            ADD_RECIPE_HEADER.prependProfile(),
            validation = String::isNotBlank,
            hint = ADD_ITEM_HINT
        ).trim()

    private fun promptAddIngredientOrFinish() =
        KInquirer.promptList(
            ACTIONS_HEADER.prependProfile(),
            listOf(
                ADD_ADDITIONAL_INGREDIENT,
                FINISH_RECIPE
            )
        ).run {
            when(this) {
                ADD_ADDITIONAL_INGREDIENT -> Action.AddItem
                else -> Action.FinishRecipe
            }
        }

    fun craftRecipe(recipes: List<RecipeStatus>) = selectRecipe(recipes)

    fun exportProfile() =
        KInquirer.promptInput(
            EXPORT_PATH,
            hint = "Press enter to cancel"
        ).run {
            ifEmpty { null }
        }

    fun importProfile() = ""

    fun displayRecipeDiff(recipeStatus: RecipeStatus) =
        StringBuilder().apply {
            val indent = recipeStatus.recipe.ingredients.maxOf { it.name.length } + 1

            appendLine()

            recipeStatus.recipe.ingredients.filter { ingredient ->
                recipeStatus.missingIngredients.none { (item, _) -> item.name == ingredient.name }
            }.forEach { ingredient ->
                appendLine(
                    (brightGreen) ("+++ ${ingredient.name}${" ".repeat(indent - ingredient.name.length)}(${displayItemQuantity(ingredient.quantity)} ${ingredient.unit})")
                )
            }
            recipeStatus.missingIngredients.forEach { (item, quantityRemaining) ->
                appendLine(
                    (brightRed) ("--- ${item.name}${" ".repeat(indent - item.name.length)}(${displayItemQuantity(item.quantity - quantityRemaining)} / ${displayItemQuantity(item.quantity)}${if (item.unit.isNullOrBlank()) "" else " ${item.unit}"})")
                )
            }
            appendLine()
        }.toString().prependIndent(INDENT)

    private fun selectRecipe(recipes: List<RecipeStatus>) : RecipeStatus? {
        val colorMap = recipes.associateBy({ colorRecipeSelection(it) }, { it })

        val selection = KInquirer.promptList(
            "Please select a recipe to craft".prependProfile(),
            colorMap.keys.toList() + CANCEL
        )

        return colorMap[selection]
    }

    private fun colorRecipeSelection(recipe: RecipeStatus) =
        if (recipe.canCraft) {
            green
        } else {
            brightRed
        }(recipe.recipe.itemName)

    fun addRecipe(inventory: Inventory) =
        addRecipeName().let { recipeName ->
            val ingredients = mutableListOf(addItem(inventory, ADD_INITIAL_INGREDIENT))

            do {
                val action = promptAddIngredientOrFinish()

                if (action == Action.AddItem) {
                    ingredients.add(addItem(inventory, ADD_INGREDIENT))
                }
            } while(action != Action.FinishRecipe)

            Recipe(recipeName, ingredients)
        }

    private fun removeOneOrMoreItems(items: List<Item>) =
        KInquirer.promptListMulti(
            "Select the item(s) you wish to remove".prependProfile(),
            items.map { it.name },
        )

    fun removeItems(items: List<Item>) =
        removeOneOrMoreItems(items) // TODO: if only one result, specify quantity to remove

    fun displayItem(item: Item) =
        table {
            cellStyle {
                border = true
                paddingLeft = 2
                paddingRight = paddingLeft
            }
            header {
                row((bold + yellow)("Name"), (bold + yellow)("Quantity"))
            }
            displayItemRow(this, item)
        }.toString().prependIndent(INDENT)

    fun displayItems(items: List<Item>) =
        if (items.none()) {
            "There are no items to display"
        } else {
            table {
                cellStyle {
                    border = true
                    paddingLeft = TABLE_PADDING
                    paddingRight = TABLE_PADDING
                }
                header {
                    row((bold + yellow)("Name"), (bold + yellow)("Quantity"))
                }
                body {
                    items.forEach {
                        displayItemRow(this, it)
                    }
                }
                footer {
                    row {
                        cell(bold("Total Items: ${items.size}")) {
                            columnSpan = 2
                        }
                    }
                }
            }.toString()
        }.prependIndent(INDENT)

    private fun displayItemRow(dsl: TableSectionDsl, item: Item) =
        dsl.row(item.name, "${displayItemQuantity(item.quantity)} ${item.unit.orEmpty()}".trim())

    private fun displayItemQuantity(quantity: Double) =
        if (quantity.mod(1.0) == 0.0) {
            quantity.toInt().toString()
        } else {
            quantity.toString()
        }

    private fun String.prependProfile() =
        "(${magenta(profileManager.currentProfile.name)}) $this"

    private fun String.removeProfile() =
        removePrefix("(${profileManager.currentProfile.name}) ")

    fun importItems() =
        KInquirer.promptInput(
            "Type the path of a CSV file containing the items.",
            hint = "Press enter to see a sample"
        ).run {
            ifEmpty { null }
        }

    fun importRecipes() =
        KInquirer.promptInput(
            "Type the path of a JSON file containing the recipes.",
            hint = "Press enter to see a sample"
        ).run {
            ifEmpty { null }
        }
}