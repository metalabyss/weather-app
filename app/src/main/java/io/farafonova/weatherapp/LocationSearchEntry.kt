package io.farafonova.weatherapp

data class LocationSearchEntry(
    val name: String,
    val state: String,
    val country: String,
    val isSelected: Boolean = false
)
