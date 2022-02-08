package io.farafonova.weatherapp

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class WeatherRepository(baseUrl: String, private val apiKey: String) {
    private val language = "en"
    private val units = "metric"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)

    companion object {
        private val TAG = WeatherRepository::class.qualifiedName
    }

    suspend fun getWeather(latitude: Float, longitude: Float): OverallWeatherResponse? {
        val response =
            weatherService.getWeather(latitude, longitude, language, units, null, apiKey)

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(TAG, "Failed to get geo data.")
            return null
        }
    }
}