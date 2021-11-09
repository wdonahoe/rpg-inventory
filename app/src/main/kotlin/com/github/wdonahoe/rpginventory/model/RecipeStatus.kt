package com.github.wdonahoe.rpginventory.model

data class RecipeStatus(
    val recipe: Recipe,
    val missingIngredients: List<Pair<Item, Double>>
) {
    val canCraft = missingIngredients.none()
}