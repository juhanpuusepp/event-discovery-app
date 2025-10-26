package com.example.evntly.data.mapper

import com.example.evntly.data.remote.NominatimPlaceDto
import com.example.evntly.domain.model.PlaceSuggestion

/**
 * Maps a raw Nominatim API DTO into a lightweight UI/domain model
 * used by the suggestions list.
 */
fun NominatimPlaceDto.toSuggestion(): PlaceSuggestion {
    val latD = lat.toDoubleOrNull() ?: 0.0
    val lonD = lon.toDoubleOrNull() ?: 0.0
    val cityLike = address?.city ?: address?.town ?: address?.village
    val country = address?.country
    val subtitle = listOfNotNull(cityLike, country).joinToString(", ").ifBlank { null }
    return PlaceSuggestion(
        title = displayName.substringBefore(",").trim(), // short primary title
        subtitle = subtitle, // secondary line
        latitude = latD,
        longitude = lonD
    )
}
