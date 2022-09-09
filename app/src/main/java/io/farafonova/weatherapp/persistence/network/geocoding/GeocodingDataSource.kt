package io.farafonova.weatherapp.persistence.network.geocoding

import android.util.Log
import io.farafonova.weatherapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object GeocodingDataSource {
    private val TAG = GeocodingDataSource::class.qualifiedName

    private const val maxNumberOfLocations: Int = 5
    private const val baseUrl = BuildConfig.OPENWEATHER_API_BASE_URL
    private const val prefix = BuildConfig.GEOCODING_PREFIX
    private const val apiKey = BuildConfig.API_KEY

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl + prefix)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private val geocodingApi: GeocodingApi = retrofit.create(GeocodingApi::class.java)

    suspend fun getLocationByName(locationName: String): List<LocationResponse>? {
        val response =
            geocodingApi.findLocationByName(locationName, maxNumberOfLocations, apiKey)

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(TAG, "Failed to get geo data.")
            return null
        }
    }
}