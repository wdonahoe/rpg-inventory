package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.model.Profile
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.RandomStringUtils
import java.io.BufferedReader

open class ProfileFileService(file: File, private val profileCreated: ((Profile) -> Unit)) : FileService(file) {

    private val _profiles = getInitialProfiles()

    val profiles : List<Profile>
        get () = _profiles

    fun createProfile(profile: String) =
        Profile(profile, RandomStringUtils.randomAlphanumeric(5)).apply {
            _profiles.add(this)

            printer.printRecord(name, folder)
            printer.flush()

            profileCreated(this)
        }

    fun deleteProfile(profile: String) {
        _profiles.removeIf { it.name == profile }
    }

    private fun getInitialProfiles() =
        BufferedReader(file.reader).use {
            CSVParser(
                it,
                CSVFormat.DEFAULT
            ).use { parser ->
                parser.mapNotNull { record ->
                    Profile(
                        name = record.get(0),
                        folder = record.get(1))
                }.toMutableList()
            }
        }
}