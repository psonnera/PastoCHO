package com.psonnera.pastocho.ui.meal

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.MealItem
import com.psonnera.pastocho.model.ModifierOption

@Stable
class MealPlannerState {
    var selectedCourse by mutableStateOf(Course.COLAZIONE)
        private set

    var selectedFood by mutableStateOf<FoodItem?>(null)
        private set

    var selectedModifiers by mutableStateOf<Map<String, ModifierOption>>(emptyMap())
        private set

    var weightInput by mutableStateOf("")
        private set

    var carbsInput by mutableStateOf("")
        private set

    var weightValue by mutableStateOf(0.0)
        private set

    var carbsValue by mutableStateOf(0.0)
        private set

    var pieceCount by mutableStateOf(1)
        private set

    var pieceWeightInput by mutableStateOf("")
        private set

    var pieceCarbsPer100gInput by mutableStateOf("")
        private set

    val mealItems = mutableStateListOf<MealItem>()

    val manualTotalCho: Double
        get() = weightValue * carbsValue / 100.0

    val totalMealCho: Double
        get() = mealItems.sumOf { it.totalCHO }

    fun selectCourse(course: Course) {
        selectedCourse = course
        selectedFood = null
        selectedModifiers = emptyMap()
        pieceCount = 1
    }

    fun selectFood(food: FoodItem) {
        selectedFood = food
        selectedModifiers = food.modifiers.associate { modifier ->
            modifier.label to modifier.options.first()
        }
        weightInput = food.defaultWeight.toString()
        weightValue = food.defaultWeight.toDouble()
        carbsInput = "%.1f".format(food.carbsPer100g.toDouble())
        carbsValue = food.carbsPer100g.toDouble()
        pieceCount = 1
        pieceWeightInput = "%.1f".format(food.defaultWeight.toDouble())
        pieceCarbsPer100gInput = "%d".format(food.carbsPer100g)
    }

    fun toggleModifier(label: String, option: ModifierOption) {
        selectedModifiers = selectedModifiers.toMutableMap().apply {
            this[label] = option
        }
    }

    fun updateManualWeightInput(input: String) {
        weightInput = input
        parseLocalizedDouble(input)?.let { weightValue = it }
    }

    fun updateManualCarbsInput(input: String) {
        carbsInput = input
        parseLocalizedDouble(input)?.let { carbsValue = it }
    }

    fun addManualMealItem() {
        mealItems.add(
            MealItem(
                name = "Manuale",
                weight = weightValue,
                totalCHO = manualTotalCho
            )
        )
        weightInput = ""
        carbsInput = ""
        weightValue = 0.0
        carbsValue = 0.0
    }

    fun updateSelectedWeightInput(input: String) {
        weightInput = input
    }

    fun incrementSelectedWeight(step: Int = 5) {
        val current = parseLocalizedDouble(weightInput) ?: 0.0
        weightInput = "%.0f".format(current + step)
    }

    fun decrementSelectedWeight(step: Int = 5) {
        val current = parseLocalizedDouble(weightInput) ?: 0.0
        weightInput = "%.0f".format(current - step)
    }

    fun decrementPieceCount() {
        pieceCount = (pieceCount - 1).coerceAtLeast(1)
    }

    fun incrementPieceCount() {
        pieceCount += 1
    }

    fun updatePieceWeightInput(input: String) {
        pieceWeightInput = input
    }

    fun updatePieceCarbsInput(input: String) {
        val number = parseLocalizedDouble(input)
        pieceCarbsPer100gInput = if (number != null) "%d".format(number.toInt()) else input
    }

    fun addSelectedFoodItem(weight: Double, totalCho: Double) {
        val food = selectedFood ?: return
        mealItems.add(
            MealItem(
                name = food.name,
                weight = weight,
                totalCHO = totalCho
            )
        )
        selectedFood = null
    }

    fun removeMealItem(item: MealItem) {
        mealItems.remove(item)
    }

    private fun parseLocalizedDouble(input: String): Double? {
        return input.replace(",", ".").toDoubleOrNull()
    }
}