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
    const val ADVANCED = "Advanced"
    const val EXIT = "Exit"
    const val CANCEL = "Cancel"
    const val BACK = "Back"

    const val ADD_ITEM_HEADER = "What is the name of the item? (start typing or press tab to display auto-completions)"
    const val ADD_ITEM_HINT = "type the name"
    const val ADD_ITEM_UNITS = "Does it have a unit? (e.g. ounce, gram etc.)"
    const val ADD_ITEM_ENTER_UNITS = "Enter the unit:"
    const val ADD_ITEM_QUANTITY = "How many"

    const val ADD_RECIPE_HEADER = "What is the name of the item created by the recipe?"
    const val ADD_INITIAL_INGREDIENT = "What is the first ingredient?"
    const val ADD_INGREDIENT = "What is the name of the ingredient?"
    const val ADD_ADDITIONAL_INGREDIENT = "Add an additional ingredient"
    const val FINISH_RECIPE = "Finish the recipe"

    const val IMPORT_PROFILE = "Import a profile (a zip archive containing recipes.json and inventory.csv)"
    const val EXPORT_PROFILE = "Export your profile"
    const val IMPORT_ITEMS = "Import items from a csv file"
    const val IMPORT_RECIPES = "Import recipes from a json file"
    const val CLEAR_INVENTORY = "Clear all items from your inventory"
    const val CLEAR_RECIPES = "Clear all recipes from your inventory"

    const val EXPORT_PATH = "Type the path to the folder you would like to export your profile to (start typing or press tab to display auto-completions, or press Enter to export to you home folder)"

    const val TABLE_PADDING = 2
    const val INDENT = "  "

    val INVENTORY_SAMPLE =
    """
        Ethereal Water,2.0,quarts
        Mudos Mushrooms,2.0,
        Garenza Leaf,3.0,
        Blood of a freshly-slain mammal,2.0,pints
        Charcoal,2.0,bricks
    """.trimIndent()

    val RECIPES_SAMPLE =
        """
[
    {
        "itemName": "Potion of Healing",
        "ingredients": [
            {
                "name": "Ethereal Water",
                "quantity": 2.0,
                "unit": "quarts"
            },
            {
                "name": "Mudos Mushroom",
                "quantity": 2.0,
                "unit": ""
            },
            {
                "name": "Garenza Leaf",
                "quantity": 3.0,
                "unit": ""
            },
            {
                "name": "Blood of a freshly-slain mammal",
                "quantity": 2.0,
                "unit": "pints"
            },
            {
                "name": "Charcoal",
                "quantity": 2.0,
                "unit": "bricks"
            }
        ]
    }
]
""".trimIndent()
}