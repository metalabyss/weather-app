package io.farafonova.weatherapp.persistence.network.weather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("exclude") excludeParts: String?,
        @Query("appid") key: String
    ): Response<OverallWeatherResponse>
}