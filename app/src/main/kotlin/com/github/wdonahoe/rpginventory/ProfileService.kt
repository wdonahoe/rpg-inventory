package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Profile
import com.github.wdonahoe.rpginventory.service.ProfileFileService

class ProfileService(private val fileService: ProfileFileService) {

    lateinit var currentProfile : Profile

    fun setProfile(profile: String) {
        val existingProfile = fileService.profiles.firstOrNull { it.name == profile }

        currentProfile = existingProfile ?: fileService.createProfile(profile)
    }

    val profiles
        get() = fileService.profiles
}