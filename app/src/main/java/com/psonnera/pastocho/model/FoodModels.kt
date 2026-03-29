package com.psonnera.pastocho.model

enum class Course(val emoji: String) {
    COLAZIONE("🥣🍪"),
    PASTO("🍝🍕"),
    MERENDA("🥞🍦"),
    FRUTTA("🍐🍑"),
    ALTRO("🍲❓")
}

data class FoodItem(
    val name: String,
    val course: Course,
    val carbsPer100g: Int,
    val defaultWeight: Int,
    val modifiers: List<FoodModifier> = emptyList(),
    val isPieceBased: Boolean = false
)

data class ModifierOption(
    val name: String,
    val percent: Int,
    val appliesTo: ModifierTarget
)

enum class ModifierTarget {
    CarbsPerGram,
    Weight
}

data class FoodModifier(
    val label: String,
    val options: List<ModifierOption>
)

data class MealItem(
    val name: String,
    val weight: Double,
    val totalCHO: Double
)