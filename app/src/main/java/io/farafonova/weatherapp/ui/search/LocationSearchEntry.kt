package io.farafonova.weatherapp.ui.search

data class LocationSearchEntry(
    val latitude: String,
    val longitude: String,
    val name: String,
    val state: String,
    val country: String,
    val isSelected: Boolean = false
)
