package com.example.evntly.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for Nominatim Search API
 * Docs: https://nominatim.org/release-docs/latest/api/Search/
 */
interface NominatimService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 10,
        // Bias results to Estonia and Estonian labels for better relevance
        @Query("countrycodes") countryCodes: String = "ee",
        @Query("accept-language") lang: String = "et",
        @Query("viewbox") viewbox: String? = null,
        @Query("bounded") bounded: Int? = null
    ): List<NominatimPlaceDto>
}
