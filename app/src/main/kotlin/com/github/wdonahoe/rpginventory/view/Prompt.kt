package com.github.wdonahoe.rpginventory.view

import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.wdonahoe.rpginventory.ProfileService
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_OPTION
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.SELECT_PROFILE_PROMPT
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer

class Prompt(private val profileService: ProfileService) {
    val welcome by lazy {
        (yellow + bold)(Values.WELCOME)
    }

    val noExistingProfileCreate by lazy {
        (brightWhite + bold)(CREATE_PROFILE_PROMPT)
    }

    val selectProfile by lazy {
        KInquirer.promptList(
            SELECT_PROFILE_PROMPT,
            profileService.profiles.mapIndexed { index, profile ->
                " ${index + 1}) ${profile.name}"
            }.plus(
                CREATE_PROFILE_OPTION
            )
        ).run {
            when(this) {
                CREATE_PROFILE_OPTION -> ProfileSelection(ProfileSelection.Operation.CreateNewProfile)
                else -> ProfileSelection(ProfileSelection.Operation.SelectProfile, split(")")[1].trim())
            }
        }
    }
}