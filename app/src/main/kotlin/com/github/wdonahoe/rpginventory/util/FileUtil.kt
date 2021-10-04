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

    private val rootDir by lazy {
        (if (SystemUtils.IS_OS_WINDOWS) {
            File(FilenameUtils.concat(System.getenv("APPDATA"), DATA_DIR))
        } else {
            File(FilenameUtils.concat(SystemUtils.USER_HOME, ".${DATA_DIR}"))
        }).apply {
            mkdir()
        }
    }

    fun File.toDisk() =
        DiskFile(this)

    fun getProfileDataFolder(profile: Profile) =
        File(rootDir, profile.folder)

    fun getTableOfContentsFile() =
        File(rootDir, TABLE_OF_CONTENTS)

    fun getInventoryCsvFile(profile: Profile) =
        File(getProfileDataFolder(profile), INVENTORY_CSV)
}