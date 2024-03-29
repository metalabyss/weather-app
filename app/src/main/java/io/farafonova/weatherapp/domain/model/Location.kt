package io.farafonova.weatherapp.domain.model


data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val state: String = "",
    val country: Country,
    val timeZoneOffset: Int = 0,
    var inFavorites: Boolean = false
)
