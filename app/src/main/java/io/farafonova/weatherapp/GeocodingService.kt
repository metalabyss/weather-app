package io.farafonova.weatherapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("direct")
    suspend fun findLocationByName(
        @Query("q") name: String,
        @Query("limit") maxNumberOfLocations: Int,
        @Query("appid") apiKey: String
    ): Response<List<Location>>
}