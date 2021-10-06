package com.github.wdonahoe.rpginventory.view

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.wdonahoe.rpginventory.ProfileManager
import com.github.wdonahoe.rpginventory.model.Item
import com.github.wdonahoe.rpginventory.view.Values.ACTIONS_HEADER
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_ENTER_UNITS
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_HEADER
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_QUANTITY
import com.github.wdonahoe.rpginventory.view.Values.ADD_ITEM_UNITS
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_OPTION
import com.github.wdonahoe.rpginventory.view.Values.CREATE_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.EXIT
import com.github.wdonahoe.rpginventory.view.Values.INDENT
import com.github.wdonahoe.rpginventory.view.Values.LIST_ITEMS
import com.github.wdonahoe.rpginventory.view.Values.SELECT_PROFILE_PROMPT
import com.github.wdonahoe.rpginventory.view.Values.SWITCH_PROFILE
import com.github.wdonahoe.rpginventory.view.Values.TABLE_PADDING
import com.github.wdonahoe.rpginventory.view.Values.WELCOME
import com.jakewharton.picnic.table
import com.yg.kotlin.inquirer.components.promptConfirm
import com.yg.kotlin.inquirer.components.promptInput
import com.yg.kotlin.inquirer.components.promptInputNumber
import com.yg.kotlin.inquirer.components.promptList
import com.yg.kotlin.inquirer.core.KInquirer

class Prompt(private val profileManager: ProfileManager) {

    val welcome by lazy {
        (TextColors.brightYellow + bold)(WELCOME)
    }

    val noExistingProfileCreate by lazy {
        (brightWhite + bold)(CREATE_PROFILE_PROMPT)
    }

    val selectProfile get() =
        KInquirer.promptList(
            SELECT_PROFILE_PROMPT.apply {
                if (profileManager.isInitialized()) {
                    prependProfile()
                }
            },
            profileManager.profiles.mapIndexed { index, profile ->
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

    val primaryActions get() =
        KInquirer.promptList(
            ACTIONS_HEADER.prependProfile(),
            listOf(
                ADD_ITEM,
                LIST_ITEMS,
                SWITCH_PROFILE,
                EXIT
            ).mapIndexed { index, action ->
                " ${index + 1}) $action"
            }
        ).run {
            when(split(")")[1].trim()) {
                ADD_ITEM -> Action.AddItem
                LIST_ITEMS -> Action.ListItems
                SWITCH_PROFILE -> Action.SelectNewProfile
                else -> Action.Exit
            }
        }

    private val addItemName get() =
        KInquirer.promptInput(
            ADD_ITEM_HEADER.prependProfile()
        ).trim()

    private val addItemHasUnit get() =
        KInquirer.promptConfirm(
            ADD_ITEM_UNITS.prependProfile(),
            default = false
        )

    private val addItemUnit get() =
        KInquirer.promptInput(
            ADD_ITEM_ENTER_UNITS.prependProfile(),
        )

    private val addItemQuantity get() =
        KInquirer.promptInputNumber(
            ADD_ITEM_QUANTITY.prependProfile(),
            default = "1"
        )

    val addItem get() =
        addItemName.let { itemName ->
            addItemHasUnit.let { hasUnit ->
                if (hasUnit) {
                    val unit = addItemUnit
                    Item(itemName, addItemQuantity.toDouble(), unit)
                } else {
                    Item(itemName, addItemQuantity.toDouble(), "")
                }
            }
        }

    fun displayItem(item: Item) =
        table {
            cellStyle {
                border = true
                paddingLeft = 2
                paddingRight = paddingLeft
            }
            header {
                row((bold + yellow)("Name"), (bold + yellow)("Quantity"))
            }
            row(item.name, "${item.quantity} ${item.unit.orEmpty()}".trim())
        }.toString().prependIndent(INDENT)

    fun displayItems(items: List<Item>) =
        table {
            cellStyle {
                border = true
                paddingLeft = TABLE_PADDING
                paddingRight = TABLE_PADDING
            }
            header {
                row((bold + yellow)("Name"), (bold + yellow)("Quantity"))
            }
            body {
                items.forEach {
                    row(it.name, "${it.quantity} ${it.unit.orEmpty()}".trim())
                }
            }
            footer {
                row {
                    cell(bold("Total Items: ${items.size}")) {
                        columnSpan = 2
                    }
                }
            }
        }.toString().prependIndent(INDENT)

    private fun String.prependProfile() =
        "(${magenta(profileManager.currentProfile.name)}) $this"

    private fun String.removeProfile() =
        this.removePrefix("(${profileManager.currentProfile.name}) ")
}