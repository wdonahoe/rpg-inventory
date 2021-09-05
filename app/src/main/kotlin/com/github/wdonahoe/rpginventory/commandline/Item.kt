package com.github.wdonahoe.rpginventory.commandline

import com.github.wdonahoe.rpginventory.model.ItemModel
import com.github.wdonahoe.rpginventory.util.UnitUtil.parseQuantityAndUnit

data class Item(
    val name: String,
    val quantity: String,
    val unit: String
)

fun Item?.withUnit() =
    if (this != null) {
        if (unit.isNotEmpty()) {
            ItemModel(name, quantity.toDoubleOrNull() ?: 1.0, unit)
        } else {
            parseQuantityAndUnit(quantity)?.let { parsed ->
                ItemModel(name, parsed.quantity, parsed.unit)
            } ?: ItemModel(name, 1.0, "")
        }
    } else {
        null
    }