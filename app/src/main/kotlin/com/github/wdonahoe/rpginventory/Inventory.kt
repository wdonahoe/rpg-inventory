package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item

class Inventory(private val fileService: FileService) {

    fun addItem(item: Item) {
        fileService.writeToInventory(item)
    }
}