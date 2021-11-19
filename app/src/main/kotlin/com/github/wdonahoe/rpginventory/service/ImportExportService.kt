package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.ProfileManager
import java.io.*
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ImportExportService(
    private val profileManager: ProfileManager,
    private val inventoryFileService: InventoryFileService,
    private val recipeFileService: RecipeFileService
) {
    private val files get() =
        listOf(
            inventoryFileService.inventory,
            recipeFileService.recipeFile
        ).map {
            (it as? DiskFile)?.path
        }

    fun export(path: String) =
        try {
            path.run {
                val zipFile = File(path, profileManager.currentProfile.name + ".zip")
                ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { out ->
                    for (file in files.filterNotNull()) {
                        FileInputStream(file).use { f ->
                            BufferedInputStream(f).use { origin ->
                                val entry = ZipEntry(file.substring(file.lastIndexOf("/")))
                                out.putNextEntry(entry)

                                origin.copyTo(out, 1024)
                            }
                        }
                    }
                }

                true to "Exported profile to ${zipFile.absolutePath}!"
            }
        } catch (ex: Exception) {
            false to ex.message
        }

    fun import(path: String) =
        Triple(false, "failure", "")
}