package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.model.RecipeStatus
import com.github.wdonahoe.rpginventory.model.plus
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService

class Inventory(
    private val inventoryService: InventoryFileService,
    private val recipeService: RecipeFileService
) {

    private val _items =
        inventoryService
            .readAll()
            .sortedBy { it.name }
            .toMutableList()

    private var _recipes =
        recipeService
            .readAll()
            .map(::getRecipeStatus)
            .sortedBy { it.recipe.itemName }
            .toMutableList()

    val items : List<Item>
        get() = _items

    val recipes : List<RecipeStatus>
        get() = _recipes

    fun addItem(item: Item) {
        val existingIndex = items.indexOfFirst {
            it.name.equals(item.name, ignoreCase = true) && it.unit?.equals(item.unit) == true
        }

        if (existingIndex != -1) {
            _items[existingIndex] += item
        } else {
            _items.add(item)
        }

        _recipes = _recipes.map { getRecipeStatus(it.recipe) }.toMutableList()

        inventoryService.writeItems(items)
    }

    fun addRecipe(recipe: Recipe) {
        _recipes.add(getRecipeStatus(recipe))

        recipeService.writeRecipes(_recipes.map { it.recipe })
    }

    private fun getRecipeStatus(recipe: Recipe) =
        RecipeStatus(
            recipe,
            recipe
                .ingredients
                .map {
                    it to getIngredientQuantityRemaining(it)
                }
                .filter { (_, quantityRemaining) ->
                    quantityRemaining != 0.0
                }
        )

    private fun getIngredientQuantityRemaining(ingredient: Item) =
        items.firstOrNull { it.name == ingredient.name }.let { item ->
            if (item == null) {
                ingredient.quantity
            } else {
                ingredient.quantity - item.quantity
            }
        }

    fun clear(): Boolean {
        _items.clear()

        return try {
            inventoryService.clearInventory()
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun export(path: String) {}

    fun removeAllItems(toRemove: List<String>) {
        _items.removeAll { toRemove.contains(it.name) }

        inventoryService.writeItems(items)
    }
}