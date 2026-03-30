package com.psonnera.pastocho.ui.meal

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.psonnera.pastocho.data.foodList
import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.MealItem
import com.psonnera.pastocho.model.ModifierOption

@Stable
class MealPlannerState {
    companion object {
        private const val MAX_WEIGHT_GRAMS = 2000.0
        private const val MAX_CARBS_PERCENT = 100.0
    }

    var selectedCourse by mutableStateOf(Course.COLAZIONE)
        private set

    val catalog = foodList

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

    val isManualInputValid: Boolean
        get() =
            manualWeightErrorMessage() == null &&
                manualCarbsErrorMessage() == null &&
                weightValue > 0 &&
                carbsValue > 0

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
        val parsed = parseLocalizedDouble(input)
        weightValue = parsed?.takeIf(::isValidWeightValue) ?: 0.0
    }

    fun updateManualCarbsInput(input: String) {
        carbsInput = input
        val parsed = parseLocalizedDouble(input)
        carbsValue = parsed?.takeIf(::isValidCarbsValue) ?: 0.0
    }

    fun addManualMealItem() {
        if (!isManualInputValid) return

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
        weightInput = "%.0f".format((current - step).coerceAtLeast(0.0))
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
        if (!isValidWeightValue(weight) || totalCho < 0) return

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

    fun computeSelectedValues(
        food: FoodItem,
        adjustedWeight: Double,
        adjustedCarbsPer100g: Double
    ): Pair<Double, Double>? {
        if (food.isPieceBased) {
            val weightPerPiece = parseAndValidateWeight(pieceWeightInput) ?: return null
            val carbsPer100g = parseAndValidateCarbs(pieceCarbsPer100gInput) ?: return null
            val effectiveWeight = pieceCount * weightPerPiece
            val totalCho = effectiveWeight * carbsPer100g / 100.0
            return effectiveWeight to totalCho
        }

        val effectiveWeight = resolveWeightOrDefault(weightInput, adjustedWeight) ?: return null
        val totalCho = effectiveWeight * adjustedCarbsPer100g / 100.0
        return effectiveWeight to totalCho
    }

    fun manualWeightErrorMessage(): String? {
        return validationMessage(
            input = weightInput,
            minExclusive = 0.0,
            maxInclusive = MAX_WEIGHT_GRAMS,
            fieldLabel = "Peso"
        )
    }

    fun manualCarbsErrorMessage(): String? {
        return validationMessage(
            input = carbsInput,
            minExclusive = 0.0,
            maxInclusive = MAX_CARBS_PERCENT,
            fieldLabel = "CHO %"
        )
    }

    fun selectedWeightErrorMessage(adjustedWeight: Double): String? {
        if (weightInput.isBlank()) {
            return if (isValidWeightValue(adjustedWeight)) null else "Peso non valido"
        }

        return validationMessage(
            input = weightInput,
            minExclusive = 0.0,
            maxInclusive = MAX_WEIGHT_GRAMS,
            fieldLabel = "Peso"
        )
    }

    fun pieceWeightErrorMessage(): String? {
        return validationMessage(
            input = pieceWeightInput,
            minExclusive = 0.0,
            maxInclusive = MAX_WEIGHT_GRAMS,
            fieldLabel = "Peso"
        )
    }

    fun pieceCarbsErrorMessage(): String? {
        return validationMessage(
            input = pieceCarbsPer100gInput,
            minExclusive = 0.0,
            maxInclusive = MAX_CARBS_PERCENT,
            fieldLabel = "CHO %"
        )
    }

    private fun parseLocalizedDouble(input: String): Double? {
        return input.replace(",", ".").toDoubleOrNull()
    }

    private fun parseAndValidateWeight(input: String): Double? {
        val parsed = parseLocalizedDouble(input) ?: return null
        return parsed.takeIf(::isValidWeightValue)
    }

    private fun parseAndValidateCarbs(input: String): Double? {
        val parsed = parseLocalizedDouble(input) ?: return null
        return parsed.takeIf(::isValidCarbsValue)
    }

    private fun resolveWeightOrDefault(input: String, defaultWeight: Double): Double? {
        if (input.isBlank()) {
            return defaultWeight.takeIf(::isValidWeightValue)
        }
        return parseAndValidateWeight(input)
    }

    private fun isValidWeightValue(value: Double): Boolean {
        return value > 0.0 && value <= MAX_WEIGHT_GRAMS
    }

    private fun isValidCarbsValue(value: Double): Boolean {
        return value > 0.0 && value <= MAX_CARBS_PERCENT
    }

    private fun validationMessage(
        input: String,
        minExclusive: Double,
        maxInclusive: Double,
        fieldLabel: String
    ): String? {
        if (input.isBlank()) {
            return "$fieldLabel richiesto"
        }

        val parsed = parseLocalizedDouble(input) ?: return "$fieldLabel non numerico"
        if (parsed <= minExclusive) {
            return "$fieldLabel deve essere > 0"
        }
        if (parsed > maxInclusive) {
            return "$fieldLabel deve essere <= ${maxInclusive.toInt()}"
        }

        return null
    }
}