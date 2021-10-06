package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.plus
import com.github.wdonahoe.rpginventory.service.InventoryFileService

class Inventory(private val fileService: InventoryFileService) {

    private val _items = fileService.readAll().condense().sortedBy { it.name }.toMutableList()

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

        fileService.writeToInventory(item)
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

    private fun Sequence<Item>.condense() =
        sequence {
            for (group in groupBy { it.name to it.unit }) {
                val quantity = group.value.sumOf { it.quantity }
                val unit = group.value.lastOrNull { it.unit != null && it.unit.isNotEmpty() }?.unit

                yield(Item(group.key.first, quantity, unit))
            }
        }

    fun export(path: String) {}
        //fileService.copyTo(path)
}