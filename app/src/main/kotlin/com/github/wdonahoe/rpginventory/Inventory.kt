package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.model.plus
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService

class Inventory(
    private val inventoryService: InventoryFileService,
    private val recipeService: RecipeFileService
) {

    private val _recipes = recipeService.readAll().sortedBy { it.itemName }.toMutableList()
    private val _items = inventoryService.readAll().sortedBy { it.name }.toMutableList()

    val items : List<Item> get() = _items
    val recipes : List<Recipe> get() = _recipes

    fun addItem(item: Item) {
        val existingIndex = items.indexOfFirst {
            it.name.equals(item.name, ignoreCase = true) && it.unit?.equals(item.unit) == true
        }

        if (existingIndex != -1) {
            _items[existingIndex] += item
        } else {
            _items.add(item)
        }

        inventoryService.writeItems(items)
    }

    fun addRecipe(recipe: Recipe) {
        _recipes.add(recipe)

        recipeService.writeRecipes(_recipes)
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