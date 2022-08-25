package io.farafonova.weatherapp.domain.model


data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val state: String,
    val country: Country,
    var inFavorites: Boolean = false
)
