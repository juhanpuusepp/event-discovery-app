package com.example.evntly.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides configured Retrofit/Moshi singletons.
 * - Moshi with KotlinJsonAdapterFactory for Kotlin data classes
 */
object RetrofitModule {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory()) // important for Kotlin data classes
        .build()

    // OkHttp client with User-Agent and timeouts
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            // Required by Nominatim policy: custom User-Agent
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "Evntly/1.0")
                    .build()
                chain.proceed(req)
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    // Retrofit service
    val nominatim: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NominatimService::class.java)
    }
}
