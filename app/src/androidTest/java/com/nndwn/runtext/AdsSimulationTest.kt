package com.nndwn.runtext

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdsSimulationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAdsDialogAppearsAfterForcingInDebug() {
        // 1. Open Sidebar
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        // 2. Navigate to Debug Panel
        val debugText = composeTestRule.activity.getString(R.string.menu_debug)
        composeTestRule.onNodeWithText(debugText).performClick()

        // 3. Force Show Ad
        composeTestRule.onNodeWithText("Force Show Ad (Reset Timer)").performClick()

        // 4. Go back to Main Screen
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 5. Click Start button on Main Screen
        val startText = composeTestRule.activity.getString(R.string.btn_start)
        composeTestRule.onNodeWithText(startText).performClick()

        // 6. Verify Ads Dialog is displayed
        val dialogTitle = composeTestRule.activity.getString(R.string.buy_coffee)
        composeTestRule.onNodeWithText(dialogTitle).assertIsDisplayed()

        // 7. Click "Skip for now"
        val skipText = composeTestRule.activity.getString(R.string.btn_maybe_later)
        composeTestRule.onNodeWithText(skipText).performClick()

        // 8. Verify we are on Display Screen (or dialog is gone)
        composeTestRule.onNodeWithText(dialogTitle).assertDoesNotExist()
    }
}
