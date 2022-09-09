package io.farafonova.weatherapp.persistence.network.weather

import android.util.Log
import io.farafonova.weatherapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object WeatherDataSource {
    private val TAG = WeatherDataSource::class.qualifiedName

    private val language = "en"
    private val units = "metric"

    private const val baseUrl = BuildConfig.OPENWEATHER_API_BASE_URL
    private const val prefix = BuildConfig.ONECALL_PREFIX
    private const val apiKey = BuildConfig.API_KEY

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl + prefix)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    private val weatherApi = retrofit.create(WeatherApi::class.java)

    suspend fun getWeather(latitude: Double, longitude: Double): OverallWeatherResponse? {
        val response =
            weatherApi.getWeather(
                latitude,
                longitude,
                language,
                units,
                null,
                apiKey
            )

        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e(TAG, "Failed to get weather data.")
            return null
        }
    }
}