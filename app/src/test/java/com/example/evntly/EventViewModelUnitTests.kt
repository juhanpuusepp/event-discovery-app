package com.example.evntly

import com.example.evntly.data.remote.NominatimService
import com.example.evntly.data.repository.EventRepository
import com.example.evntly.domain.model.Event
import com.example.evntly.data.remote.RetrofitModule
import com.example.evntly.ui.viewmodel.EventViewModel
import io.mockk.*
import org.junit.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class EventViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: EventRepository
    private lateinit var nominatim: NominatimService
    private lateinit var viewModel: EventViewModel

    private val eventsFlow = MutableStateFlow<List<Event>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        nominatim = mockk()

        every { repository.getAllEvents() } returns eventsFlow

        // Swap RetrofitModule singleton for tests
        mockkObject(RetrofitModule)
        every { RetrofitModule.nominatim } returns nominatim

        viewModel = EventViewModel(repository)
    }

    // Helper for creating events in tests
    fun fakeEvent(
        id: Int = 1,
        name: String = "Test Event",
        date: Date = Date(0),
        price: Double = 0.0,
        description: String = "Test Description",
        location: String = "Tartu",
        latitude: Double = 59.437,
        longitude: Double = 24.7536
    ): Event {
        return Event(
            id = id,
            name = name,
            date = date,
            price = price,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // -------------------------------------------------------------
    // Events tests
    // -------------------------------------------------------------

    @Test
    fun `addEvent calls repository`() = runTest {
        val event = fakeEvent(id = 2, name = "New")

        coEvery { repository.addEvent(event) } just Runs

        viewModel.addEvent(event)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.addEvent(event) }
    }

    @Test
    fun `deleteEvent calls repository`() = runTest {
        val event = fakeEvent(id = 3, name = "Old")

        coEvery { repository.deleteEvent(event) } just Runs

        viewModel.deleteEvent(event)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.deleteEvent(event) }
    }

    @Test
    fun `clearPlaceSuggestions resets state`() {
        viewModel.clearPlaceSuggestions()

        assertEquals(
            EventViewModel.PlaceUiState(),
            viewModel.placeUiState.value
        )
    }
}