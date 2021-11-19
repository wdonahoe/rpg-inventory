package com.github.wdonahoe.rpginventory.view

object Values {
    private const val APP_NAME = "RPG Inventory"

    const val WELCOME = "Welcome to $APP_NAME!"

    const val CREATE_PROFILE_OPTION = "Create a new profile"
    const val CREATE_PROFILE_PROMPT = "Create a profile (e.g, the name of a character or campaign): "
    const val SELECT_PROFILE_PROMPT = "Select your profile: "

    const val ACTIONS_HEADER = "Select an action:"
    const val ADD_ITEM = "Add an item"
    const val ADD_RECIPE = "Add a recipe"
    const val CRAFT_ITEM = "Craft an item"
    const val REMOVE_ITEMS = "Remove items"
    const val LIST_ITEMS = "Display items"
    const val SWITCH_PROFILE = "Switch to a different profile"
    const val ADVANCED = "Advanced actions"
    const val EXIT = "Exit"
    const val CANCEL = "Cancel"
    const val BACK = "Back"

    const val ADD_ITEM_HEADER = "What is the name of the item?"
    const val ADD_ITEM_HINT = "type the name"
    const val ADD_ITEM_UNITS = "Does it have a unit? (e.g. ounce, gram etc.)"
    const val ADD_ITEM_ENTER_UNITS = "Enter the unit:"
    const val ADD_ITEM_QUANTITY = "How many?"

    const val ADD_RECIPE_HEADER = "What is the name of the item created by the recipe?"
    const val ADD_INITIAL_INGREDIENT = "What is the first ingredient?"
    const val ADD_INGREDIENT = "What is the name of the ingredient?"
    const val ADD_ADDITIONAL_INGREDIENT = "Add an additional ingredient"
    const val FINISH_RECIPE = "Finish the recipe"

    const val IMPORT_PROFILE = "Import a profile"
    const val EXPORT_PROFILE = "Export a profile"

    const val EXPORT_PATH = "Enter the path of the directory you would like to export your profile to"

    const val TABLE_PADDING = 2
    const val INDENT = "  "
}