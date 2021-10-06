package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import org.junit.Assert
import org.junit.Test

class InventoryTests {
    private val inventory
        get() = Inventory(InventoryFileService(TestUtil.throwawayFile))

    @Test
    fun testCreate() {
        with(inventory) {
            Assert.assertEquals(items.size, 0)
        }
    }

    @Test
    fun testAdd() {
        with(inventory) {
            addItem(Item("item 1", 1.0, "oz"))

            Assert.assertEquals(items.size, 1)
            items[0].apply {
                Assert.assertEquals(name, "item 1")
                Assert.assertEquals(quantity, 1.0, 0.0)
                Assert.assertEquals(unit, "oz")
            }
        }
    }

    @Test
    fun testAddSame() {
        with(inventory) {
            addItem(Item("item 1", 1.0, "oz"))
            addItem(Item("item 1", 2.0, "oz"))

            Assert.assertEquals(items.size, 1)
            items[0].apply {
                Assert.assertEquals(name, "item 1")
                Assert.assertEquals(quantity, 3.0, 0.0)
                Assert.assertEquals(unit, "oz")
            }
        }
    }

    @Test
    fun testAddNotSame() {
        with(inventory) {
            addItem(Item("item 1", 1.0, "oz"))
            addItem(Item("item 2", 2.0, "lb"))

            Assert.assertEquals(items.size, 2)

            items[0].apply {
                Assert.assertEquals(name, "item 1")
                Assert.assertEquals(quantity, 1.0, 0.0)
                Assert.assertEquals(unit, "oz")
            }

            items[1].apply {
                Assert.assertEquals(name, "item 2")
                Assert.assertEquals(quantity, 2.0, 0.0)
                Assert.assertEquals(unit, "lb")
            }
        }
    }
}