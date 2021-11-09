package com.github.wdonahoe.rpginventory

import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.model.Recipe
import com.github.wdonahoe.rpginventory.service.InventoryFileService
import com.github.wdonahoe.rpginventory.service.RecipeFileService
import org.junit.Assert
import org.junit.Test

class InventoryTests {
    private val inventory
        get() = Inventory(InventoryFileService(TestUtil.throwawayFile), RecipeFileService(TestUtil.throwawayFile))

    @Test
    fun testCreate() {
        with(inventory) {
            Assert.assertEquals(items.size, 0)
            Assert.assertEquals(recipes.size, 0)
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

    @Test
    fun testAddRecipe() {
        with(inventory) {
            addRecipe(
                Recipe(
                    "test",
                    listOf(
                        Item(
                            "ingredient 1",
                            1.0,
                            "oz"
                        ),
                        Item(
                            "ingredient 2",
                            2.0,
                            null
                        )
                    )
                )
            )

            Assert.assertEquals(recipes.size, 1)

            Assert.assertEquals(recipes[0].recipe.ingredients[0].name, "ingredient 1")
            Assert.assertEquals(recipes[0].recipe.ingredients[1].name, "ingredient 2")
        }
    }
}