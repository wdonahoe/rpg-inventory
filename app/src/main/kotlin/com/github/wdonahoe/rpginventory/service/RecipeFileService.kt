package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.model.Recipe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class RecipeFileService(val recipeFile: File) {
    fun readAll() : List<Recipe> =
        BufferedReader(recipeFile.reader).use { reader ->
            reader.readText().let { json ->
                if (json.isEmpty()) {
                    listOf()
                }
                else {
                    Json.decodeFromString(json)
                }
            }
        }

    fun writeRecipes(recipes: List<Recipe>) {
        BufferedWriter(recipeFile.getWriter(append = false)).use { bufferedWriter ->
            bufferedWriter.write(Json.encodeToString(recipes))
        }
    }

    fun clearRecipes() {
        (recipeFile as? DiskFile)?.let { diskFile ->
            Files.newBufferedWriter(java.io.File(diskFile.path).toPath(), StandardOpenOption.TRUNCATE_EXISTING).use {  }
        }
    }
}