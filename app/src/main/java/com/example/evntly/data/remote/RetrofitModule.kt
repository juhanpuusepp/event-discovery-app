import com.example.evntly.data.remote.NominatimService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Provides configured Retrofit/Moshi singletons.
 * - Moshi with KotlinJsonAdapterFactory for Kotlin data classes
 */
object RetrofitModule {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory()) // important for Kotlin data classes
        .build()

    val nominatim: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NominatimService::class.java)
    }
}
