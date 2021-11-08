package com.github.wdonahoe.rpginventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val itemName: String,
    val ingredients: List<Item>
)