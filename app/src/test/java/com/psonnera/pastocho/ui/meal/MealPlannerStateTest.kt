package com.psonnera.pastocho.ui.meal

import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.FoodModifier
import com.psonnera.pastocho.model.ModifierOption
import com.psonnera.pastocho.model.ModifierTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MealPlannerStateTest {

    @Test
    fun selectFoodInitializesDependentState() {
        val state = MealPlannerState()
        val food = FoodItem(
            name = "Biscotti",
            course = Course.COLAZIONE,
            carbsPer100g = 70,
            defaultWeight = 10,
            isPieceBased = true,
            modifiers = listOf(
                FoodModifier(
                    label = "Dimensione",
                    options = listOf(
                        ModifierOption("Media", 0, ModifierTarget.Weight),
                        ModifierOption("Grande", 50, ModifierTarget.Weight)
                    )
                )
            )
        )

        state.incrementPieceCount()
        state.selectFood(food)

        assertEquals(food, state.selectedFood)
        assertEquals("10", state.weightInput)
        assertEquals("70.0", state.carbsInput)
        assertEquals("10.0", state.pieceWeightInput)
        assertEquals("70", state.pieceCarbsPer100gInput)
        assertEquals(1, state.pieceCount)
        assertEquals("Media", state.selectedModifiers["Dimensione"]?.name)
    }

    @Test
    fun addManualMealItemAppendsEntryAndClearsInputs() {
        val state = MealPlannerState()

        state.updateManualWeightInput("120")
        state.updateManualCarbsInput("25")
        state.addManualMealItem()

        assertEquals(1, state.mealItems.size)
        assertEquals("Manuale", state.mealItems.single().name)
        assertEquals(120.0, state.mealItems.single().weight, 0.001)
        assertEquals(30.0, state.mealItems.single().totalCHO, 0.001)
        assertEquals("", state.weightInput)
        assertEquals("", state.carbsInput)
        assertEquals(0.0, state.weightValue, 0.001)
        assertEquals(0.0, state.carbsValue, 0.001)
    }

    @Test
    fun addSelectedFoodItemClearsCurrentSelection() {
        val state = MealPlannerState()
        val food = FoodItem(
            name = "Pane",
            course = Course.PASTO,
            carbsPer100g = 50,
            defaultWeight = 50
        )

        state.selectFood(food)
        state.addSelectedFoodItem(weight = 75.0, totalCho = 37.5)

        assertNull(state.selectedFood)
        assertEquals(1, state.mealItems.size)
        assertEquals("Pane", state.mealItems.single().name)
        assertEquals(75.0, state.mealItems.single().weight, 0.001)
        assertEquals(37.5, state.mealItems.single().totalCHO, 0.001)
    }

    @Test
    fun manualValidationRejectsOutOfRangeValues() {
        val state = MealPlannerState()

        state.updateManualWeightInput("-10")
        state.updateManualCarbsInput("120")

        assertNotNull(state.manualWeightErrorMessage())
        assertNotNull(state.manualCarbsErrorMessage())
        assertEquals(false, state.isManualInputValid)

        state.addManualMealItem()
        assertEquals(0, state.mealItems.size)
    }

    @Test
    fun computeSelectedValuesReturnsNullForInvalidWeightInput() {
        val state = MealPlannerState()
        val food = FoodItem(
            name = "Pane",
            course = Course.PASTO,
            carbsPer100g = 50,
            defaultWeight = 50
        )

        state.selectFood(food)
        state.updateSelectedWeightInput("-5")

        val values = state.computeSelectedValues(
            food = food,
            adjustedWeight = 50.0,
            adjustedCarbsPer100g = 50.0
        )

        assertNull(values)
    }
}