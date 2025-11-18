package com.example.evntly

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.*
import com.example.evntly.domain.model.PlaceSuggestion
import com.example.evntly.ui.screens.add.AddEventScreen
import com.example.evntly.ui.viewmodel.EventViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class AddEventScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Mock the ViewModel and its state
    private lateinit var viewModel: EventViewModel
    private lateinit var placeUiStateFlow: MutableStateFlow<EventViewModel.PlaceUiState>
    private lateinit var selectedDateFlow: MutableStateFlow<Date?>

    @Before
    fun setUp() {
        // Prepare the mock ViewModel before each test
        placeUiStateFlow = MutableStateFlow(EventViewModel.PlaceUiState())
        selectedDateFlow = MutableStateFlow(null) 
        viewModel = mockk(relaxed = true) {
            every { placeUiState } returns placeUiStateFlow
            every { selectedDate } returns selectedDateFlow
            every { searchPlacesDebounced(any()) } answers {
                // Simulate the ViewModel getting suggestions
                placeUiStateFlow.value = EventViewModel.PlaceUiState(
                    suggestions = listOf(
                        PlaceSuggestion("Tartu, Estonia", "Tartumaa", 59.43, 24.75)
                    )
                )
            }
            // Mock the function that the DatePicker would call to update the state
            every { setSelectedDate(any()) } answers {
                selectedDateFlow.value = firstArg()
            }
        }
    }

    @Test
    fun addEventScreen_submitsForm_whenValid() {
        var onBackCalled = false

        composeRule.setContent {
            AddEventScreen(
                onBack = { onBackCalled = true },
                viewModel = viewModel
            )
        }

        // 1. Fill out the form fields using test tags
        composeRule.onNodeWithTag("name_input").performTextInput("Concert")
        composeRule.onNodeWithTag("price_input").performTextInput("12.5")
        composeRule.onNodeWithTag("description_input").performTextInput("Live music event")

        composeRule.onNodeWithTag("location_input").performTextInput("Tartu")
        composeRule.waitForIdle()

        // 2. Simulate selecting a location from the suggestion list
        composeRule.onNodeWithText("Tartu, Estonia").performClick()
        composeRule.waitForIdle()

        // 3. Verify that the location text was updated after selection
        composeRule.onNodeWithTag("location_input").assertTextContains("Tartu, Estonia, Tartumaa")

        // 4. Mock the date selection instead of trying to click through dialogs
        // Create a test date to use
        val testDate = Calendar.getInstance().time

        // Run the state update on the main UI thread for Compose state changes
        composeRule.runOnUiThread {
            viewModel.setSelectedDate(testDate)
        }
        composeRule.waitForIdle()

        // 5. Verify the "Save Event" button is now enabled because all fields are valid
        composeRule.onNodeWithTag("save_button").assertIsEnabled()

        // 6. Click the save button
        composeRule.onNodeWithTag("save_button").performClick()
        composeRule.waitForIdle()

        // 7. Verify the final actions
        verify { viewModel.addEvent(any()) }
        assertTrue("onBack should have been called after saving.", onBackCalled)
    }
}
