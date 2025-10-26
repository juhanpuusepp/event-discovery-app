package com.example.evntly.data.remote

import com.squareup.moshi.Json

/**
 * Minimal fields from Nominatim JSON output
 * JSON format reference: https://nominatim.org/release-docs/latest/api/Output/
 *
 * Keep strings for lat/lon (as Nominatim returns strings), convert later
 */
data class NominatimPlaceDto(
    @Json(name = "place_id") val placeId: Long,
    @Json(name = "display_name") val displayName: String,
    val lat: String,
    val lon: String,
    val address: AddressDto?
) {
    /**
     * Flattened subset of address fields to build a human-friendly subtitle
     */
    data class AddressDto(
        val house_number: String?,
        val road: String?,
        val city: String?,
        val town: String?,
        val village: String?,
        val state: String?,
        val postcode: String?,
        val country: String?
    )
}
