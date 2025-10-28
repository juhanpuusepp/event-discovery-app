package com.example.evntly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evntly.domain.model.Event
import com.example.evntly.data.repository.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.evntly.data.mapper.toSuggestion
import com.example.evntly.domain.model.PlaceSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import com.example.evntly.data.remote.RetrofitModule
import retrofit2.HttpException

/**
 * Viewmodel - a bridge between View and Model
 * Lets the UI communicate to the back-end
 */
class EventViewModel(private val repository: EventRepository) : ViewModel() {
    // Events list from Room
    val events: StateFlow<List<Event>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addEvent(event: Event) {
        viewModelScope.launch {
            repository.addEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // Place search UI state
    data class PlaceUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val suggestions: List<PlaceSuggestion> = emptyList()
    )

    private val _placeUiState = MutableStateFlow(PlaceUiState())
    val placeUiState = _placeUiState.asStateFlow()

    // Simple retry/backoff helper (retry once on 503/429)
    private suspend fun <T> withRetryOnce(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: HttpException) {
            if (e.code() == 503 || e.code() == 429) {
                delay(2000) // brief backoff
                block()
            } else {
                throw e
            }
        }
    }

    /**
     * Debounced search against Nominatim
     */
    fun searchPlacesDebounced(query: String) {
        if (query.length < 3) {
            _placeUiState.value = PlaceUiState() // clear suggestions & error
            return
        }
        viewModelScope.launch {
            _placeUiState.update { it.copy(isLoading = true, error = null, suggestions = emptyList()) }
            try {
                // simple debounce to stay under 1 req/sec while typing
                delay(800)

                // Estonia viewbox (left,top,right,bottom) - helps reduce slow, global searches -> fewer 503s
                val viewbox = "21.5,59.8,28.2,57.3" // rough EE bounds
                val bounded = 1

                val results = withRetryOnce {
                    RetrofitModule.nominatim
                        .search(
                            query = query,
                            viewbox = viewbox,
                            bounded = bounded
                        )
                }.map { it.toSuggestion() }

                _placeUiState.update { it.copy(isLoading = false, suggestions = results, error = null) }
            } catch (e: Exception) {
                _placeUiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Network error")
                }
            }
        }
    }

    /**
     * Clears suggestions after the user selects a place.
     */
    fun clearPlaceSuggestions() {
        _placeUiState.value = PlaceUiState()
    }
}
