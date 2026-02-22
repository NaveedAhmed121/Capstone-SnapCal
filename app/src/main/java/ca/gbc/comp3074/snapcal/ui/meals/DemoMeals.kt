package ca.gbc.comp3074.snapcal.ui.meals

/**
 * Demo in-app "Menu" database that mirrors your HTML prototype.
 * Keep it local/offline so it always works for marking.
 */
object DemoMeals {

    data class Ingredient(
        val name: String,
        val quantity: Double,
        val unit: String,
        val category: Category
    )

    enum class Category { meat, seafood, dairy, vegetables, fruits, pantry }

    enum class MealType { breakfast, lunch, dinner, snack }

    data class Meal(
        val id: Int,
        val name: String,
        val type: MealType,
        val calories: Int,
        val icon: String,
        val ingredients: List<Ingredient>,
        val recipeSteps: List<String>
    )

    val all: List<Meal> = listOf(
        // BREAKFAST
        Meal(
            id = 1,
            name = "Power Breakfast Bowl",
            type = MealType.breakfast,
            calories = 420,
            icon = "🍳",
            ingredients = listOf(
                Ingredient("Large eggs", 3.0, "pcs", Category.dairy),
                Ingredient("Turkey bacon", 4.0, "strips", Category.meat),
                Ingredient("Medium potatoes", 2.0, "pcs", Category.vegetables),
                Ingredient("Cheddar cheese", 50.0, "g", Category.dairy),
                Ingredient("Olive oil", 1.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Heat oil in pan",
                "Cook diced potatoes until golden",
                "Add beaten eggs and scramble",
                "Cook bacon separately",
                "Top with cheese and bacon"
            )
        ),
        Meal(
            id = 2,
            name = "Overnight Oats Berry Boost",
            type = MealType.breakfast,
            calories = 350,
            icon = "🥣",
            ingredients = listOf(
                Ingredient("Rolled oats", 80.0, "g", Category.pantry),
                Ingredient("Greek yogurt", 150.0, "g", Category.dairy),
                Ingredient("Mixed berries", 100.0, "g", Category.fruits),
                Ingredient("Honey", 2.0, "tbsp", Category.pantry),
                Ingredient("Chia seeds", 1.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Mix oats with yogurt",
                "Add honey and chia seeds",
                "Layer with berries",
                "Refrigerate overnight",
                "Enjoy cold in the morning"
            )
        ),
        Meal(
            id = 3,
            name = "Avocado Toast Deluxe",
            type = MealType.breakfast,
            calories = 380,
            icon = "🥑",
            ingredients = listOf(
                Ingredient("Whole grain bread", 2.0, "slices", Category.pantry),
                Ingredient("Ripe avocado", 1.0, "pc", Category.fruits),
                Ingredient("Cherry tomatoes", 6.0, "pcs", Category.vegetables),
                Ingredient("Feta cheese", 30.0, "g", Category.dairy),
                Ingredient("Lemon juice", 1.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Toast bread until golden",
                "Mash avocado with lemon",
                "Spread on toast",
                "Top with tomatoes and feta",
                "Season with salt and pepper"
            )
        ),
        Meal(
            id = 4,
            name = "Protein Smoothie Bowl",
            type = MealType.breakfast,
            calories = 320,
            icon = "🥤",
            ingredients = listOf(
                Ingredient("Protein powder", 30.0, "g", Category.pantry),
                Ingredient("Banana", 1.0, "pc", Category.fruits),
                Ingredient("Spinach", 50.0, "g", Category.vegetables),
                Ingredient("Almond milk", 250.0, "ml", Category.dairy),
                Ingredient("Granola", 30.0, "g", Category.pantry),
            ),
            recipeSteps = listOf(
                "Blend protein powder with milk",
                "Add banana and spinach",
                "Blend until smooth",
                "Pour into bowl",
                "Top with granola"
            )
        ),
        Meal(
            id = 5,
            name = "Veggie Scramble Wrap",
            type = MealType.breakfast,
            calories = 360,
            icon = "🌯",
            ingredients = listOf(
                Ingredient("Large eggs", 2.0, "pcs", Category.dairy),
                Ingredient("Whole wheat tortilla", 1.0, "pc", Category.pantry),
                Ingredient("Bell pepper", 0.5, "pc", Category.vegetables),
                Ingredient("Mushrooms", 100.0, "g", Category.vegetables),
                Ingredient("Spinach", 50.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Sauté vegetables until tender",
                "Beat and scramble eggs",
                "Combine eggs with vegetables",
                "Warm tortilla",
                "Wrap filling in tortilla"
            )
        ),
        Meal(
            id = 6,
            name = "Quinoa Breakfast Bowl",
            type = MealType.breakfast,
            calories = 390,
            icon = "🍲",
            ingredients = listOf(
                Ingredient("Quinoa", 60.0, "g", Category.pantry),
                Ingredient("Almond milk", 200.0, "ml", Category.dairy),
                Ingredient("Blueberries", 80.0, "g", Category.fruits),
                Ingredient("Almonds", 20.0, "g", Category.pantry),
                Ingredient("Cinnamon", 1.0, "tsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Cook quinoa in almond milk",
                "Add cinnamon while cooking",
                "Top with fresh blueberries",
                "Sprinkle with almonds",
                "Serve warm"
            )
        ),
        Meal(
            id = 7,
            name = "Greek Yogurt Parfait",
            type = MealType.breakfast,
            calories = 300,
            icon = "🥜",
            ingredients = listOf(
                Ingredient("Greek yogurt", 200.0, "g", Category.dairy),
                Ingredient("Granola", 40.0, "g", Category.pantry),
                Ingredient("Strawberries", 100.0, "g", Category.fruits),
                Ingredient("Honey", 1.0, "tbsp", Category.pantry),
                Ingredient("Walnuts", 15.0, "g", Category.pantry),
            ),
            recipeSteps = listOf(
                "Layer yogurt in glass",
                "Add granola layer",
                "Add strawberry layer",
                "Repeat layers",
                "Top with honey and walnuts"
            )
        ),

        // LUNCH
        Meal(
            id = 8,
            name = "Grilled Chicken Caesar",
            type = MealType.lunch,
            calories = 450,
            icon = "🥗",
            ingredients = listOf(
                Ingredient("Chicken breast", 150.0, "g", Category.meat),
                Ingredient("Romaine lettuce", 200.0, "g", Category.vegetables),
                Ingredient("Parmesan cheese", 30.0, "g", Category.dairy),
                Ingredient("Caesar dressing", 2.0, "tbsp", Category.pantry),
                Ingredient("Croutons", 20.0, "g", Category.pantry),
            ),
            recipeSteps = listOf(
                "Season and grill chicken",
                "Chop romaine lettuce",
                "Slice grilled chicken",
                "Toss lettuce with dressing",
                "Top with chicken, cheese, croutons"
            )
        ),
        Meal(
            id = 9,
            name = "Turkey Club Wrap",
            type = MealType.lunch,
            calories = 420,
            icon = "🥙",
            ingredients = listOf(
                Ingredient("Turkey breast", 120.0, "g", Category.meat),
                Ingredient("Whole wheat wrap", 1.0, "pc", Category.pantry),
                Ingredient("Avocado", 0.5, "pc", Category.fruits),
                Ingredient("Tomato", 1.0, "pc", Category.vegetables),
                Ingredient("Lettuce", 50.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Lay out wrap",
                "Spread mashed avocado",
                "Add turkey slices",
                "Add tomato and lettuce",
                "Roll wrap tightly"
            )
        ),
        Meal(
            id = 10,
            name = "Quinoa Buddha Bowl",
            type = MealType.lunch,
            calories = 480,
            icon = "🍜",
            ingredients = listOf(
                Ingredient("Quinoa", 80.0, "g", Category.pantry),
                Ingredient("Chickpeas", 150.0, "g", Category.pantry),
                Ingredient("Sweet potato", 150.0, "g", Category.vegetables),
                Ingredient("Kale", 100.0, "g", Category.vegetables),
                Ingredient("Tahini", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Cook quinoa according to package",
                "Roast sweet potato cubes",
                "Massage kale with oil",
                "Combine all ingredients",
                "Drizzle with tahini dressing"
            )
        ),
        Meal(
            id = 11,
            name = "Salmon Poke Bowl",
            type = MealType.lunch,
            calories = 520,
            icon = "🍣",
            ingredients = listOf(
                Ingredient("Fresh salmon", 150.0, "g", Category.seafood),
                Ingredient("Sushi rice", 80.0, "g", Category.pantry),
                Ingredient("Cucumber", 100.0, "g", Category.vegetables),
                Ingredient("Edamame", 80.0, "g", Category.vegetables),
                Ingredient("Soy sauce", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Cook sushi rice",
                "Dice fresh salmon",
                "Slice cucumber",
                "Steam edamame",
                "Assemble bowl with soy sauce"
            )
        ),
        Meal(
            id = 12,
            name = "Mediterranean Bowl",
            type = MealType.lunch,
            calories = 460,
            icon = "🫒",
            ingredients = listOf(
                Ingredient("Brown rice", 80.0, "g", Category.pantry),
                Ingredient("Grilled chicken", 120.0, "g", Category.meat),
                Ingredient("Feta cheese", 50.0, "g", Category.dairy),
                Ingredient("Olives", 30.0, "g", Category.pantry),
                Ingredient("Cucumber", 100.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Cook brown rice",
                "Grill and slice chicken",
                "Dice cucumber",
                "Combine all ingredients",
                "Add olive oil and herbs"
            )
        ),
        Meal(
            id = 13,
            name = "Veggie Lentil Soup",
            type = MealType.lunch,
            calories = 380,
            icon = "🍲",
            ingredients = listOf(
                Ingredient("Red lentils", 100.0, "g", Category.pantry),
                Ingredient("Vegetable broth", 500.0, "ml", Category.pantry),
                Ingredient("Carrots", 100.0, "g", Category.vegetables),
                Ingredient("Celery", 80.0, "g", Category.vegetables),
                Ingredient("Onion", 1.0, "pc", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Sauté diced vegetables",
                "Add lentils and broth",
                "Simmer for 20 minutes",
                "Season with herbs",
                "Serve hot"
            )
        ),

        // DINNER
        Meal(
            id = 14,
            name = "Herb Crusted Salmon",
            type = MealType.dinner,
            calories = 580,
            icon = "🐟",
            ingredients = listOf(
                Ingredient("Salmon fillet", 200.0, "g", Category.seafood),
                Ingredient("Asparagus", 200.0, "g", Category.vegetables),
                Ingredient("Baby potatoes", 150.0, "g", Category.vegetables),
                Ingredient("Olive oil", 2.0, "tbsp", Category.pantry),
                Ingredient("Fresh herbs", 20.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Season salmon with herbs",
                "Roast potatoes and asparagus",
                "Pan-sear salmon",
                "Cook until flaky",
                "Serve with roasted vegetables"
            )
        ),
        Meal(
            id = 15,
            name = "Chicken Stir-fry",
            type = MealType.dinner,
            calories = 520,
            icon = "🍗",
            ingredients = listOf(
                Ingredient("Chicken breast", 180.0, "g", Category.meat),
                Ingredient("Mixed vegetables", 250.0, "g", Category.vegetables),
                Ingredient("Brown rice", 80.0, "g", Category.pantry),
                Ingredient("Soy sauce", 3.0, "tbsp", Category.pantry),
                Ingredient("Garlic", 3.0, "cloves", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Cook brown rice",
                "Cut chicken into strips",
                "Stir-fry chicken until golden",
                "Add vegetables and garlic",
                "Finish with soy sauce"
            )
        ),
        Meal(
            id = 16,
            name = "Lean Beef & Sweet Potato",
            type = MealType.dinner,
            calories = 620,
            icon = "🥩",
            ingredients = listOf(
                Ingredient("Lean beef", 180.0, "g", Category.meat),
                Ingredient("Sweet potato", 200.0, "g", Category.vegetables),
                Ingredient("Green beans", 150.0, "g", Category.vegetables),
                Ingredient("Red wine", 50.0, "ml", Category.pantry),
                Ingredient("Thyme", 1.0, "tsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Season beef with thyme",
                "Roast sweet potato wedges",
                "Sear beef to desired doneness",
                "Steam green beans",
                "Deglaze pan with wine"
            )
        ),
        Meal(
            id = 17,
            name = "Shrimp Zucchini Noodles",
            type = MealType.dinner,
            calories = 380,
            icon = "🍤",
            ingredients = listOf(
                Ingredient("Large shrimp", 200.0, "g", Category.seafood),
                Ingredient("Zucchini", 300.0, "g", Category.vegetables),
                Ingredient("Cherry tomatoes", 150.0, "g", Category.vegetables),
                Ingredient("Garlic", 4.0, "cloves", Category.vegetables),
                Ingredient("Olive oil", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Spiralize zucchini into noodles",
                "Sauté garlic in olive oil",
                "Add shrimp and cook",
                "Add tomatoes and zucchini",
                "Cook until shrimp is pink"
            )
        ),
        Meal(
            id = 18,
            name = "Turkey Meatballs & Pasta",
            type = MealType.dinner,
            calories = 560,
            icon = "🍝",
            ingredients = listOf(
                Ingredient("Ground turkey", 150.0, "g", Category.meat),
                Ingredient("Whole wheat pasta", 80.0, "g", Category.pantry),
                Ingredient("Marinara sauce", 150.0, "ml", Category.pantry),
                Ingredient("Mozzarella cheese", 40.0, "g", Category.dairy),
                Ingredient("Basil", 10.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf(
                "Form turkey into meatballs",
                "Cook pasta according to package",
                "Brown meatballs in pan",
                "Add marinara sauce",
                "Serve over pasta with cheese"
            )
        ),
        Meal(
            id = 19,
            name = "Grilled Portobello Stack",
            type = MealType.dinner,
            calories = 350,
            icon = "🍄",
            ingredients = listOf(
                Ingredient("Portobello mushrooms", 2.0, "pcs", Category.vegetables),
                Ingredient("Eggplant", 150.0, "g", Category.vegetables),
                Ingredient("Bell peppers", 150.0, "g", Category.vegetables),
                Ingredient("Goat cheese", 60.0, "g", Category.dairy),
                Ingredient("Balsamic vinegar", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Grill mushrooms and vegetables",
                "Layer vegetables on mushroom",
                "Top with goat cheese",
                "Drizzle with balsamic",
                "Serve immediately"
            )
        ),
        Meal(
            id = 20,
            name = "Cod with Roasted Vegetables",
            type = MealType.dinner,
            calories = 420,
            icon = "🐠",
            ingredients = listOf(
                Ingredient("Cod fillet", 180.0, "g", Category.seafood),
                Ingredient("Brussels sprouts", 200.0, "g", Category.vegetables),
                Ingredient("Carrots", 150.0, "g", Category.vegetables),
                Ingredient("Lemon", 1.0, "pc", Category.fruits),
                Ingredient("Olive oil", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf(
                "Roast vegetables with olive oil",
                "Season cod with lemon",
                "Bake cod for 15 minutes",
                "Check fish is flaky",
                "Serve with roasted vegetables"
            )
        ),

        // SNACKS
        Meal(
            id = 21,
            name = "Apple Almond Butter",
            type = MealType.snack,
            calories = 220,
            icon = "🍎",
            ingredients = listOf(
                Ingredient("Apple", 1.0, "pc", Category.fruits),
                Ingredient("Almond butter", 2.0, "tbsp", Category.pantry),
            ),
            recipeSteps = listOf("Core and slice apple", "Serve with almond butter for dipping")
        ),
        Meal(
            id = 22,
            name = "Greek Yogurt Berry Cup",
            type = MealType.snack,
            calories = 180,
            icon = "🫐",
            ingredients = listOf(
                Ingredient("Greek yogurt", 150.0, "g", Category.dairy),
                Ingredient("Mixed berries", 80.0, "g", Category.fruits),
                Ingredient("Honey", 1.0, "tsp", Category.pantry),
            ),
            recipeSteps = listOf("Add berries to yogurt", "Drizzle with honey", "Mix gently")
        ),
        Meal(
            id = 23,
            name = "Hummus Veggie Plate",
            type = MealType.snack,
            calories = 190,
            icon = "🥕",
            ingredients = listOf(
                Ingredient("Hummus", 60.0, "g", Category.pantry),
                Ingredient("Carrots", 100.0, "g", Category.vegetables),
                Ingredient("Cucumber", 100.0, "g", Category.vegetables),
                Ingredient("Bell pepper", 80.0, "g", Category.vegetables),
            ),
            recipeSteps = listOf("Cut vegetables into sticks", "Arrange on plate", "Serve with hummus")
        ),
        Meal(
            id = 24,
            name = "Protein Energy Balls",
            type = MealType.snack,
            calories = 240,
            icon = "⚽",
            ingredients = listOf(
                Ingredient("Dates", 80.0, "g", Category.fruits),
                Ingredient("Almonds", 30.0, "g", Category.pantry),
                Ingredient("Protein powder", 15.0, "g", Category.pantry),
                Ingredient("Coconut flakes", 15.0, "g", Category.pantry),
            ),
            recipeSteps = listOf(
                "Blend dates and almonds",
                "Add protein powder",
                "Form into balls",
                "Roll in coconut",
                "Chill for 30 minutes"
            )
        ),
        Meal(
            id = 25,
            name = "Avocado Toast Bites",
            type = MealType.snack,
            calories = 160,
            icon = "🥪",
            ingredients = listOf(
                Ingredient("Whole grain crackers", 6.0, "pcs", Category.pantry),
                Ingredient("Avocado", 0.5, "pc", Category.fruits),
                Ingredient("Cherry tomatoes", 3.0, "pcs", Category.vegetables),
                Ingredient("Sea salt", 0.5, "tsp", Category.pantry),
            ),
            recipeSteps = listOf("Mash avocado with salt", "Spread on crackers", "Top with tomato slices", "Serve immediately")
        )
    )
}
