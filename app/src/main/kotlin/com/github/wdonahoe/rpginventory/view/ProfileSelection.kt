package com.github.wdonahoe.rpginventory.view

data class ProfileSelection(
    val operation: Operation,
    val profile: String = ""
) {
    enum class Operation {
        CreateNewProfile,
        SelectProfile,
        ImportProfile
    }
}