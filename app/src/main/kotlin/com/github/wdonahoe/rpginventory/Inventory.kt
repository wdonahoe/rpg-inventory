package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.plus
import com.github.wdonahoe.rpginventory.service.InventoryFileService

class Inventory(private val fileService: InventoryFileService) {

    private val _items = fileService.readAll().sortedBy { it.name }.toMutableList()

    val items : List<Item> get() = _items

    fun addItem(item: Item) {
        val existingIndex = items.indexOfFirst {
            it.name.equals(item.name, ignoreCase = true) && it.unit?.equals(item.unit) == true
        }

        if (existingIndex != -1) {
            _items[existingIndex] += item
        } else {
            _items.add(item)
        }

        fileService.writeItems(items)
    }

    fun clear(): Boolean {
        _items.clear()

        return try {
            fileService.clearInventory()
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun export(path: String) {}

    fun removeAllItems(toRemove: List<String>) {
        _items.removeAll { toRemove.contains(it.name) }

        fileService.writeItems(items)
    }
}