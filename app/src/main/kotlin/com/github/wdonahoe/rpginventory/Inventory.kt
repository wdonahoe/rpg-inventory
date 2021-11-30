package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.model.RecipeStatus
import com.github.wdonahoe.rpginventory.model.plus
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService

class Inventory(
    val inventoryService: InventoryFileService,
    val recipeService: RecipeFileService
) {

    private var _items =
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

    fun addItems(items: List<Item>) {
        for (item in items) {
            addItem(item)
        }

        inventoryService.writeItems(this.items)
    }

    fun addItem(item: Item, write: Boolean = true) {
        val existingIndex = items.indexOfFirst {
            it.name.equals(item.name, ignoreCase = true) && (it.unit == null || it.unit == item.unit)
        }

        if (existingIndex != -1) {
            _items[existingIndex] += item
        } else {
            _items.add(item)
        }

        _recipes = _recipes.map { getRecipeStatus(it.recipe) }.toMutableList()

        if (write) {
            inventoryService.writeItems(items)
        }
    }

    fun addRecipe(recipe: Recipe, write: Boolean = true) {
        _recipes.add(getRecipeStatus(recipe))

        if (write) {
            recipeService.writeRecipes(_recipes.map { it.recipe })
        }
    }

    fun getUnit(itemName: String) =
        items.firstOrNull { it.name.equals(itemName, ignoreCase = true) }?.unit ?: recipes.firstOrNull { it.recipe.ingredients.any { ingredient -> ingredient.name.equals(itemName, ignoreCase = true)} }?.recipe?.ingredients?.first { it.name.equals(itemName, ignoreCase = true) }?.unit

    private fun getRecipeStatus(recipe: Recipe) =
        RecipeStatus(
            recipe,
            recipe
                .ingredients
                .map {
                    it to getIngredientQuantityRemaining(it)
                }
                .filter { (_, quantityRemaining) ->
                    quantityRemaining > 0
                }
        )

    private fun getIngredientQuantityRemaining(ingredient: Item) =
        items.firstOrNull { it.name.equals(ingredient.name, ignoreCase = true) }.let { item ->
            if (item == null) {
                ingredient.quantity
            } else {
                ingredient.quantity - item.quantity
            }
        }

    fun clearRecipes() : Pair<Boolean, String?> {
        _recipes.clear()

        return try {
            recipeService.clearRecipes()

            true to null
        } catch (ex: Exception) {
            false to ex.message
        }
    }

    fun clearItems(): Pair<Boolean, String?> {
        _items.clear()

        return try {
            inventoryService.clearInventory()

            true to null
        } catch (ex: Exception) {
            false to ex.message
        }
    }

    fun removeAllItems(toRemove: List<String>) {
        _items.removeAll { toRemove.contains(it.name) }

        inventoryService.writeItems(items)
    }

    private fun removeItems(toRemove: List<Item>) {
        for (item in toRemove) {
            val toUpdate = items.first { it.name.equals(item.name, ignoreCase = true) }
            toUpdate.quantity -= item.quantity

            if (toUpdate.quantity <= 0.0) {
                _items.remove(toUpdate)
            }
        }

        _recipes = _recipes.map { getRecipeStatus(it.recipe) }.toMutableList()

        inventoryService.writeItems(items)
    }

    fun craftRecipe(recipe: Recipe) {
        removeItems(recipe.ingredients)

        addItem(
            Item(
                recipe.itemName,
                1.0,
                null
            )
        )
    }

    fun addRecipes(recipes: List<Recipe>) {
        for (recipe in recipes) {
            addRecipe(recipe, write = false)
        }

        recipeService.writeRecipes(_recipes.map { it.recipe })
    }
}