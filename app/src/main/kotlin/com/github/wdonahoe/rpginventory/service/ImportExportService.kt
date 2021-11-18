package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.Inventory
import com.github.wdonahoe.rpginventory.ProfileManager

class ImportExportService(
    private val profileManager: ProfileManager,
    private val inventory: Inventory
) {
    fun export() =
        false

    fun import() =
        Triple(false, "failure", "")
}