package com.github.wdonahoe.rpginventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val name: String,
    var quantity: Double,
    val unit: String?
)

operator fun Item.plus(item: Item) =
    copy(
        name = name,
        quantity = quantity + item.quantity,
        unit = unit
    )

typealias ItemModel = Item
