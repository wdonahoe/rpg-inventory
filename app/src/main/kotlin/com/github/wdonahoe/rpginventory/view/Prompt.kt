package com.github.wdonahoe.rpginventory.view

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.bold
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
import com.github.wdonahoe.rpginventory.view.Values.CANCEL
import com.github.wdonahoe.rpginventory.view.Values.CRAFT_ITEM
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_OPTION
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.EXIT
import com.github.wdonahoe.rpginventory.view.Values.FINISH_RECIPE
import com.github.wdonahoe.rpginventory.view.Values.INDENT
import com.github.wdonahoe.rpginventory.view.Values.LIST_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.REMOVE_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.SELECT_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.SWITCH_PROFILE
import com.github.wdonahoe.rpginventory.view.Values.TABLE_PADDING
import com.github.wdonahoe.rpginventory.view.Values.WELCOME
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

    val primaryActions get() =
        KInquirer.promptList(
            ACTIONS_HEADER.prependProfile(),
            listOf(
                ADD_ITEM,
                ADD_RECIPE,
                CRAFT_ITEM,
                REMOVE_ITEMS,
                LIST_ITEMS,
                SWITCH_PROFILE,
                EXIT
            ).mapIndexed { index, action ->
                " ${index + 1}) $action"
            }
        ).run {
            when(split(")")[1].trim()) {
                ADD_ITEM -> Action.AddItem
                ADD_RECIPE -> Action.AddRecipe
                CRAFT_ITEM -> Action.CraftItem
                REMOVE_ITEMS -> Action.RemoveItem
                LIST_ITEMS -> Action.ListItems
                SWITCH_PROFILE -> Action.SelectNewProfile
                else -> Action.Exit
            }
        }

    private fun addItemName(prompt: String) =
        KInquirer.promptInput(
            prompt.prependProfile(),
            validation = String::isNotBlank,
            hint = ADD_ITEM_HINT
        ).trim()

    private val addItemHasUnit get() =
        KInquirer.promptConfirm(
            ADD_ITEM_UNITS.prependProfile(),
            default = false
        )

    private val addItemUnit get() =
        KInquirer.promptInput(
            ADD_ITEM_ENTER_UNITS.prependProfile(),
        )

    private val addItemQuantity get() =
        KInquirer.promptInputNumber(
            ADD_ITEM_QUANTITY.prependProfile(),
            default = "1"
        )

    fun addItem(prompt: String = ADD_ITEM_HEADER) =
        addItemName(prompt).let { itemName ->
            addItemHasUnit.let { hasUnit ->
                if (hasUnit) {
                    val unit = addItemUnit
                    Item(itemName, addItemQuantity.toDouble(), unit)
                } else {
                    Item(itemName, addItemQuantity.toDouble(), "")
                }
            }
        }

    private val addRecipeName get() =
        KInquirer.promptInput(
            ADD_RECIPE_HEADER.prependProfile(),
            validation = String::isNotBlank,
            hint = ADD_ITEM_HINT
        ).trim()

    private val promptAddIngredientOrFinish get() =
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

    fun craftRecipe(recipes: List<RecipeStatus>) =
        selectRecipe(recipes)?.let { recipeStatus ->
            if (recipeStatus.canCraft) {
                recipeStatus.recipe
            } else {
                null
            }
        }

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

    fun addRecipe() =
        addRecipeName.let { recipeName ->
            val ingredients = mutableListOf(addItem(ADD_INITIAL_INGREDIENT))

            do {
                val action = promptAddIngredientOrFinish

                if (action == Action.AddItem) {
                    ingredients.add(addItem(ADD_INGREDIENT))
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
            row(item.name, "${item.quantity} ${item.unit.orEmpty()}".trim())
        }.toString().prependIndent(INDENT)

    fun displayItems(items: List<Item>) =
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
                    row(it.name, "${it.quantity} ${it.unit.orEmpty()}".trim())
                }
            }
            footer {
                row {
                    cell(bold("Total Items: ${items.size}")) {
                        columnSpan = 2
                    }
                }
            }
        }.toString().prependIndent(INDENT)

    private fun String.prependProfile() =
        "(${magenta(profileManager.currentProfile.name)}) $this"

    private fun String.removeProfile() =
        this.removePrefix("(${profileManager.currentProfile.name}) ")
}