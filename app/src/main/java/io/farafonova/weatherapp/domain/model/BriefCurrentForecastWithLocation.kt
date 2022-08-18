package io.farafonova.weatherapp.domain.model

data class BriefCurrentForecastWithLocation(
    val temperature: Int,
    val location: Location
)
