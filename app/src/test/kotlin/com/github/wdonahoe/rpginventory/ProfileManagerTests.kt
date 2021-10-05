package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.service.TableOfContentsFileService
import org.junit.Assert
import org.junit.Test

class ProfileManagerTests {

    private val profileService
        get() = ProfileManager(TableOfContentsFileService(TestUtil.throwawayFile) { })

    @Test
    fun testCreate() {
        with(profileService) {
            Assert.assertFalse(isInitialized())
            Assert.assertEquals(profiles.size, 0)
        }
    }

    @Test
    fun testCreateFirstProfile() {
        with(profileService) {
            setProfile("first")

            Assert.assertTrue(isInitialized())
            Assert.assertEquals(profiles.size, 1)
            Assert.assertEquals(currentProfile.name, "first")
        }
    }

    @Test
    fun testAddProfile() {
        with(profileService) {
            setProfile("first")
            setProfile("second")
            setProfile("third")

            Assert.assertTrue(isInitialized())
            Assert.assertEquals(profiles.size, 3)
            Assert.assertEquals(currentProfile.name, "third")
        }
    }
}