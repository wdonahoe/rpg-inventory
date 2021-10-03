package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.model.Profile
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.RandomStringUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class ProfileFileService : FileService() {

    override val file by lazy {
        File(rootDir, TABLE_OF_CONTENTS).apply {
            createNewFile()
        }
    }

    private val _profiles = getInitialProfiles()

    val profiles : List<Profile>
        get () = _profiles

    fun createProfile(profile: String) =
        Profile(profile, RandomStringUtils.randomAlphanumeric(5)).apply {
            _profiles.add(this)

            printer.printRecord(name, folder)
            printer.flush()

            File(rootDir, folder).mkdir()
        }

    fun deleteProfile(profile: String) {
        _profiles.removeIf { it.name == profile }
    }

    private fun getInitialProfiles() =
        BufferedReader(FileReader(file)).use {
            CSVParser(
                it,
                CSVFormat.DEFAULT
            ).use { parser ->
                parser.map { record ->
                    Profile(
                        name = record.get(0),
                        folder = record.get(1))
                }.toMutableList()
            }
        }

    companion object {
        private const val TABLE_OF_CONTENTS = "toc"
    }
}