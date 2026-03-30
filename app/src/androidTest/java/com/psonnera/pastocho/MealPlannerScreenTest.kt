package com.psonnera.pastocho

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class MealPlannerScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun manualEntryAddsMealItemAndUpdatesTotal() {
        composeTestRule.onNodeWithTag("course-altro").performClick()

        composeTestRule.onNodeWithTag("manual-weight-input").performTextInput("100")
        composeTestRule.onNodeWithTag("manual-carbs-input").performTextInput("20")

        composeTestRule.onNodeWithText("CHO totale: 20.0").assertIsDisplayed()

        composeTestRule.onNodeWithTag("manual-add-button").performClick()

        composeTestRule.onNodeWithText("Manuale").assertIsDisplayed()
        composeTestRule.onNodeWithText("Totale CHO: 20").assertIsDisplayed()
    }

    @Test
    fun selectingCatalogFoodAddsExpectedChoTotal() {
        composeTestRule.onNodeWithTag("course-pasto").performClick()
        composeTestRule.onNodeWithTag("food-dropdown-trigger").performClick()
        composeTestRule.onNodeWithText("Pane").performClick()

        composeTestRule.onNodeWithTag("selected-weight-input").performTextClearance()
        composeTestRule.onNodeWithTag("selected-weight-input").performTextInput("50")
        composeTestRule.onNodeWithTag("selected-food-add-button").performClick()

        composeTestRule.onNodeWithText("Pane").assertIsDisplayed()
        composeTestRule.onNodeWithText("Totale CHO: 25").assertIsDisplayed()
    }

    @Test
    fun manualInvalidInputDisablesAddButton() {
        composeTestRule.onNodeWithTag("course-altro").performClick()

        composeTestRule.onNodeWithTag("manual-weight-input").performTextInput("-5")
        composeTestRule.onNodeWithTag("manual-carbs-input").performTextInput("120")

        composeTestRule.onNodeWithTag("manual-add-button").assertIsNotEnabled()
    }
}