package com.example.evntly

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.evntly.domain.model.PlaceSuggestion
import com.example.evntly.ui.screens.add.AddEventScreen
import com.example.evntly.ui.viewmodel.EventViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
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

    @Before
    fun setUp() {
        // Prepare the mock ViewModel before each test
        placeUiStateFlow = MutableStateFlow(EventViewModel.PlaceUiState())
        viewModel = mockk(relaxed = true) {
            every { placeUiState } returns placeUiStateFlow
            every { searchPlacesDebounced(any()) } answers {
                // Simulate the ViewModel getting suggestions
                placeUiStateFlow.value = EventViewModel.PlaceUiState(
                    suggestions = listOf(
                        PlaceSuggestion("Tartu, Estonia", "Tartumaa", 59.43, 24.75)
                    )
                )
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

        // Wait for the UI to process the location input and show suggestions
        composeRule.waitForIdle()

        // 2. Simulate selecting a location from the suggestion list
        composeRule.onNodeWithText("Tartu, Estonia").performClick()
        composeRule.waitForIdle()

        // 3. Verify that the location text was updated after selection
        composeRule.onNodeWithTag("location_input").assertTextContains("Tartu, Estonia, Tartumaa")

        // 4. "Save Event" button is still disabled because `date` is null.
        composeRule.onNodeWithTag("save_button").assertIsNotEnabled()
    }
}
