package com.github.wdonahoe.rpginventory.util

import com.github.wdonahoe.rpginventory.model.Profile
import com.github.wdonahoe.rpginventory.service.DiskFile
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import java.io.File

object FileUtil {

    private const val DATA_DIR = "rpginventory"
    private const val TABLE_OF_CONTENTS = "toc"
    private const val INVENTORY_CSV = "inventory.csv"
    private const val RECIPES_JSON = "recipes.json"

    val rootDir by lazy {
        (if (SystemUtils.IS_OS_WINDOWS) {
            File(FilenameUtils.concat(System.getenv("APPDATA"), DATA_DIR))
        } else {
            File(FilenameUtils.concat(SystemUtils.USER_HOME, ".${DATA_DIR}"))
        }).apply {
            mkdir()
        }
    }

    private fun File.toDisk() =
        DiskFile(this)

    fun getProfileDataFolder(profile: Profile) =
        File(rootDir, profile.folder).apply {
            mkdir()
        }

    fun getTableOfContentsFile() =
        File(rootDir, TABLE_OF_CONTENTS).apply {
            createNewFile()
        }.toDisk()

    fun getInventoryFile(profile: Profile) =
        File(File(rootDir, profile.folder), INVENTORY_CSV).apply {
            createNewFile()
        }.toDisk()

    fun getRecipeFile(profile: Profile) =
        File(File(rootDir, profile.folder), RECIPES_JSON).apply {
            createNewFile()
        }.toDisk()
}