package com.github.wdonahoe.rpginventory.model

data class Item(
    val name: String,
    val quantity: Double,
    val unit: String?
)

operator fun Item.plus(item: Item) =
    copy(
        name = name,
        quantity = quantity + item.quantity,
        unit = unit
    )

typealias ItemModel = Item
