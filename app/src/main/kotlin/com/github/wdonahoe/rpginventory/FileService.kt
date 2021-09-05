package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import java.io.File

class FileService {

    private val inventory = if (SystemUtils.IS_OS_WINDOWS) {
        File(FilenameUtils.concat(System.getenv("APPDATA"), DATA_DIR), INVENTORY_CSV)
    } else {
        File(FilenameUtils.concat(SystemUtils.USER_HOME, ".${DATA_DIR}"), INVENTORY_CSV)
    }

    init {
        if (!inventory.parentFile.exists()) {
            inventory.mkdirs()
        }
    }

    fun writeToInventory(item: Item) {

    }

    companion object {
        const val INVENTORY_CSV = "inventory.csv"
        const val DATA_DIR = "rpginventory"
    }
}