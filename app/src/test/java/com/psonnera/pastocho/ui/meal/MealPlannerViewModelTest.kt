package com.psonnera.pastocho.ui.meal

import org.junit.Assert.assertEquals
import org.junit.Test

class MealPlannerViewModelTest {

    @Test
    fun exposesStateThatCanBeUpdated() {
        val viewModel = MealPlannerViewModel()

        viewModel.state.updateManualWeightInput("100")
        viewModel.state.updateManualCarbsInput("20")

        assertEquals("100", viewModel.state.weightInput)
        assertEquals("20", viewModel.state.carbsInput)
        assertEquals(20.0, viewModel.state.manualTotalCho, 0.001)
    }
}