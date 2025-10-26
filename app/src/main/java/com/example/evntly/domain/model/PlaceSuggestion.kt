package com.example.evntly.domain.model

/**
 * Lightweight model to render an autocomplete suggestion in the UI
 */
data class PlaceSuggestion(
    val title: String,        // primary line
    val subtitle: String?,    // secondary line (city, country)
    val latitude: Double,
    val longitude: Double
)
