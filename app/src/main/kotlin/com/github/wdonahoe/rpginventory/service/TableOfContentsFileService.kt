package com.github.wdonahoe.rpginventory.service

import com.github.wdonahoe.rpginventory.model.Profile
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.RandomStringUtils
import java.io.BufferedReader
import java.io.BufferedWriter

class TableOfContentsFileService(
    private val tableOfContents: File,
    private val profileCreatedCallback: ((Profile) -> Unit)
) {

    private val _profiles = getInitialProfiles()

    private val csvPrinter
        get() =
            CSVPrinter(
                BufferedWriter(tableOfContents.getWriter(append = true)),
                CSVFormat.DEFAULT
            )

    val profiles : List<Profile>
        get () = _profiles

    fun createProfile(profile: String) =
        Profile(profile, RandomStringUtils.randomAlphanumeric(5)).apply {
            _profiles.add(this)
            writeProfile(this)
            profileCreatedCallback(this)
        }

    private fun writeProfile(profile: Profile) {
        csvPrinter.use { printer ->
            printer.printRecord(profile.name, profile.folder)
            printer.flush()
        }
    }

    fun deleteProfile(profile: String) {
        _profiles.removeIf { it.name == profile }
    }

    private fun getInitialProfiles() =
        BufferedReader(tableOfContents.reader).use {
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