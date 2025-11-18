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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val queryFlow = MutableStateFlow("")
    private val cache = mutableMapOf<String, List<PlaceSuggestion>>()

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(400) // faster debounce
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    /**
     * Debounced search against Nominatim
     */
    fun searchPlacesDebounced(query: String) {
        queryFlow.value = query
    }

    private suspend fun performSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 3) {
            _placeUiState.value = PlaceUiState()
            return
        }

        // Check cache first
        cache[trimmed.lowercase()]?.let { cached ->
            _placeUiState.value = PlaceUiState(isLoading = false, suggestions = cached)
            return
        }

        _placeUiState.update { it.copy(isLoading = true, error = null) }

        try {
            val viewbox = "21.5,59.8,28.2,57.3"
            val bounded = 1

            val results = withRetryOnce {
                RetrofitModule.nominatim.search(
                    query = trimmed,
                    viewbox = viewbox,
                    bounded = bounded
                )
            }.map { it.toSuggestion() }

            cache[trimmed.lowercase()] = results

            _placeUiState.update {
                it.copy(isLoading = false, suggestions = results, error = null)
            }
        } catch (e: HttpException) {
            val msg = when (e.code()) {
                400 -> "Bad request — Nominatim rejected the query."
                429 -> "Rate limit reached — please wait a few seconds."
                else -> "Server error (${e.code()})"
            }
            _placeUiState.update { it.copy(isLoading = false, error = msg) }
        } catch (e: Exception) {
            _placeUiState.update {
                it.copy(isLoading = false, error = e.message ?: "Network error")
            }
        }
    }

    private suspend fun <T> withRetryOnce(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: HttpException) {
            if (e.code() == 503 || e.code() == 429) {
                delay(1500)
                block()
            } else throw e
        }
    }

    /**
     * Clears suggestions after the user selects a place.
     */
    fun clearPlaceSuggestions() {
        _placeUiState.value = PlaceUiState()
    }
}
