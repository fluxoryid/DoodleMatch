package com.fluxoryid.doodlematch.domain

data class DrawObject(
    val id: String,
    val title: String,
    val symbol: String
)

object DoodleCatalog {
    private val catalog = mapOf(
        "animals" to listOf(
            DrawObject("elephant", "Elephant", "🐘"),
            DrawObject("lion", "Lion", "🦁"),
            DrawObject("cat", "Cat", "🐱"),
            DrawObject("dog", "Dog", "🐶"),
            DrawObject("fish", "Fish", "🐟")
        ),
        "dinosaurs" to listOf(
            DrawObject("trex", "T-Rex", "🦖"),
            DrawObject("sauropod", "Sauropod", "🦕"),
            DrawObject("triceratops", "Triceratops", "🦕"),
            DrawObject("egg", "Dino Egg", "🥚"),
            DrawObject("fossil", "Fossil", "🦴")
        ),
        "vehicles" to listOf(
            DrawObject("car", "Car", "🚗"),
            DrawObject("bus", "Bus", "🚌"),
            DrawObject("train", "Train", "🚆"),
            DrawObject("airplane", "Airplane", "✈️"),
            DrawObject("boat", "Boat", "⛵")
        ),
        "fruits" to listOf(
            DrawObject("apple", "Apple", "🍎"),
            DrawObject("banana", "Banana", "🍌"),
            DrawObject("strawberry", "Strawberry", "🍓"),
            DrawObject("grapes", "Grapes", "🍇"),
            DrawObject("watermelon", "Watermelon", "🍉")
        ),
        "space" to listOf(
            DrawObject("rocket", "Rocket", "🚀"),
            DrawObject("planet", "Planet", "🪐"),
            DrawObject("star", "Star", "⭐"),
            DrawObject("moon", "Moon", "🌙"),
            DrawObject("alien", "Alien", "👽")
        ),
        "abc_numbers" to listOf(
            DrawObject("letter_a", "Letter A", "A"),
            DrawObject("letter_b", "Letter B", "B"),
            DrawObject("letter_c", "Letter C", "C"),
            DrawObject("number_1", "Number 1", "1"),
            DrawObject("number_2", "Number 2", "2")
        )
    )

    fun objectsFor(categoryId: String): List<DrawObject> = catalog[categoryId].orEmpty()

    fun categoryTitle(categoryId: String): String = when (categoryId) {
        "animals" -> "Animals"
        "dinosaurs" -> "Dinosaurs"
        "vehicles" -> "Vehicles"
        "fruits" -> "Fruits"
        "space" -> "Space"
        "abc_numbers" -> "ABC & Numbers"
        else -> categoryId.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
}
