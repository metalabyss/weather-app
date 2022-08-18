package io.farafonova.weatherapp.domain.model


data class Location(
    val latitude: Float,
    val longitude: Float,
    val name: String,
    val state: String,
    val country: Country,
    var inFavorites: Boolean = false
)
