package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Profile
import com.github.wdonahoe.rpginventory.service.ProfileFileService

class ProfileService(private val fileService: ProfileFileService) {

    lateinit var currentProfile : Profile

    fun isInitialized() = this::currentProfile.isInitialized

    fun setProfile(profile: String) {
        val existingProfile = fileService.profiles.firstOrNull { it.name == profile }

        currentProfile = existingProfile ?: fileService.createProfile(profile)
    }

    fun useFirstProfile() {
        currentProfile = profiles.first()
    }

    val profiles
        get() = fileService.profiles
}