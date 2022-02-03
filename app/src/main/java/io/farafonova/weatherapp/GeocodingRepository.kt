package io.farafonova.weatherapp

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class GeocodingRepository(private val baseUrl: String, private val apiKey: String) {
    private val maxNumberOfLocations: Int = 5
    private val TAG = GeocodingRepository::class.qualifiedName

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private val geocodingService: GeocodingService = retrofit.create(GeocodingService::class.java)

    suspend fun getLocationByName(locationName: String): List<Location>? {
        val response =
            geocodingService.findLocationByName(locationName, maxNumberOfLocations, apiKey)

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(TAG, "Failed to get data.")
            return null
        }
    }
}