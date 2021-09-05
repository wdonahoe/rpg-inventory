package com.github.wdonahoe.rpginventory.model

data class Item(
    val name: String,
    val quantity: Double,
    val unit: String?
)

typealias ItemModel = Item
