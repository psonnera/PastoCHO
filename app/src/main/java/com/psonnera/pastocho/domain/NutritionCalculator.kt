package com.psonnera.pastocho.domain

import com.psonnera.pastocho.model.FoodItem
import com.psonnera.pastocho.model.ModifierTarget
import com.psonnera.pastocho.model.ModifierOption

fun calculateAdjustedValues(
    food: FoodItem,
    selectedModifiers: List<ModifierOption>
): Pair<Double, Double> {
    var weightMultiplier = 1.0
    var carbsMultiplier = 1.0

    selectedModifiers.forEach { modifier ->
        val factor = 1 + (modifier.percent / 100.0)
        when (modifier.appliesTo) {
            ModifierTarget.Weight -> weightMultiplier *= factor
            ModifierTarget.CarbsPerGram -> carbsMultiplier *= factor
        }
    }

    val adjustedWeight = food.defaultWeight * weightMultiplier
    val adjustedCarbsPerGram = food.carbsPer100g * carbsMultiplier

    return adjustedWeight to adjustedCarbsPerGram
}