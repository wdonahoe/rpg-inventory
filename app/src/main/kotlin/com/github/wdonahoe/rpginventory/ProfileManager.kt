package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Profile
import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import com.github.wdonahoe.rpginventory.util.FileUtil
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.zip.ZipFile

class ProfileManager(private val fileService: TableOfContentsFileService) {

    val profiles
        get() = fileService.profiles

    lateinit var currentProfile : Profile

    fun isInitialized() = this::currentProfile.isInitialized

    fun setProfile(profile: String) {
        val existingProfile = fileService.profiles.firstOrNull { it.name == profile }

        currentProfile = existingProfile ?: fileService.createProfile(profile)
    }

    fun useFirstProfile() {
        currentProfile = profiles.first()
    }

    fun importProfile(path: String) =
        try {
            path.run {
                val file = File(path)
                val zipCopy = file.copyTo(File(FilenameUtils.concat(FileUtil.rootDir.absolutePath, file.name)), overwrite = true)

                ZipFile(zipCopy).use { zip ->
                    setProfile(file.nameWithoutExtension)

                    zip.entries().asSequence().forEach { entry ->
                        zip.getInputStream(entry).use { input ->
                            File(entry.name).outputStream().use { output ->
                                input.copyTo(output, 2048)
                            }
                        }
                    }
                }

                true to null
            }
        } catch (ex: Exception) {
            false to ex.message
        }

}