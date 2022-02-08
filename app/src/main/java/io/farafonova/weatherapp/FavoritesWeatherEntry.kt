package io.farafonova.weatherapp

data class FavoritesWeatherEntry(
    val latitude: String,
    val longitude: String,
    val locationName: String,
    val temperature: Int
)
