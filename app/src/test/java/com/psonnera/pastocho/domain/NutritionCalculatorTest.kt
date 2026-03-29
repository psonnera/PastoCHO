package com.psonnera.pastocho.domain

import com.psonnera.pastocho.model.Course
import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.ModifierOption
import com.psonnera.pastocho.model.ModifierTarget
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionCalculatorTest {

    private val pasta = FoodItem(
        name = "Pasta",
        course = Course.PASTO,
        carbsPer100g = 30,
        defaultWeight = 200
    )

    @Test
    fun appliesWeightModifierToDefaultWeight() {
        val modifiers = listOf(
            ModifierOption(
                name = "Grande",
                percent = 25,
                appliesTo = ModifierTarget.Weight
            )
        )

        val (adjustedWeight, adjustedCarbsPer100g) = calculateAdjustedValues(pasta, modifiers)

        assertEquals(250.0, adjustedWeight, 0.001)
        assertEquals(30.0, adjustedCarbsPer100g, 0.001)
    }

    @Test
    fun appliesCarbModifierToCarbsPer100g() {
        val modifiers = listOf(
            ModifierOption(
                name = "Senza Sugo",
                percent = -20,
                appliesTo = ModifierTarget.CarbsPerGram
            )
        )

        val (adjustedWeight, adjustedCarbsPer100g) = calculateAdjustedValues(pasta, modifiers)

        assertEquals(200.0, adjustedWeight, 0.001)
        assertEquals(24.0, adjustedCarbsPer100g, 0.001)
    }

    @Test
    fun combinesWeightAndCarbModifiersMultiplicatively() {
        val modifiers = listOf(
            ModifierOption(
                name = "Large",
                percent = 10,
                appliesTo = ModifierTarget.Weight
            ),
            ModifierOption(
                name = "Rich Sauce",
                percent = 20,
                appliesTo = ModifierTarget.CarbsPerGram
            ),
            ModifierOption(
                name = "Extra Portion",
                percent = 10,
                appliesTo = ModifierTarget.Weight
            )
        )

        val (adjustedWeight, adjustedCarbsPer100g) = calculateAdjustedValues(pasta, modifiers)

        assertEquals(242.0, adjustedWeight, 0.001)
        assertEquals(36.0, adjustedCarbsPer100g, 0.001)
    }
}