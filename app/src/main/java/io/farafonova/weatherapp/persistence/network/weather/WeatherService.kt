package io.farafonova.weatherapp.persistence.network.weather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("exclude") excludeParts: String?,
        @Query("appid") key: String
    ): Response<OverallWeatherResponse>
}