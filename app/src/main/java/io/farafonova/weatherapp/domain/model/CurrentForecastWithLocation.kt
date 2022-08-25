package io.farafonova.weatherapp.domain.model


data class CurrentForecastWithLocation(
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val forecastTime: String,
    val windSpeed: Double,
    val windDegree: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Int,
    val uvi: Double,
    val description: String,
    val location: Location
)
