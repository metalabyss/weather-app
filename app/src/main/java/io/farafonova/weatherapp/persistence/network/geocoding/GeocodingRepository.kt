package io.farafonova.weatherapp.persistence.network.geocoding

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class GeocodingRepository(baseUrl: String, private val apiKey: String) {
    private val maxNumberOfLocations: Int = 5

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private val geocodingService: GeocodingService = retrofit.create(GeocodingService::class.java)

    suspend fun getLocationByName(locationName: String): List<LocationResponse>? {
        val response =
            geocodingService.findLocationByName(locationName, maxNumberOfLocations, apiKey)

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(TAG, "Failed to get geo data.")
            return null
        }
    }

    companion object {
        private val TAG = GeocodingRepository::class.qualifiedName
    }
}