package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.ProfileManager
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.util.FileUtil
import java.io.*
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ImportExportService(
    private val profileManager: ProfileManager,
    private val inventoryFileService: InventoryFileService,
    private val recipeFileService: RecipeFileService
) {
    private val files get() =
        listOf(
            inventoryFileService.inventoryFile,
            recipeFileService.recipeFile
        ).map {
            (it as? DiskFile)?.path
        }

    fun export(path: String) =
        try {
            path.run {
                val zipFile = File(path, profileManager.currentProfile.name + ".zip")

                ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { out ->
                    for (file in files.filterNotNull().map { File(it) }) {
                        FileInputStream(file).use { f ->
                            BufferedInputStream(f).use { origin ->
                                out.putNextEntry(ZipEntry(file.name))

                                origin.copyTo(out, 2048)
                            }
                        }
                    }
                }

                true to "Exported profile to ${zipFile.absolutePath}!"
            }
        } catch (ex: Exception) {
            false to "Export failed: ${ex.message}"
        }

    fun importItems(path: String) =
        try {
            inventoryFileService.readAll(DiskFile(File(path)).reader).let { items ->
                true to items
            }
        } catch (ex: Exception) {
            false to listOf()
        }

    fun importRecipes(path: String) =
        try {
            recipeFileService.readAll(DiskFile(File(path)).reader).let { recipes ->
                true to recipes
            }
        } catch (ex: Exception) {
            false to listOf()
        }
}