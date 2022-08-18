package io.farafonova.weatherapp.domain.model


data class CurrentForecastWithLocation(
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val forecastTime: String,
    val windSpeed: Float,
    val windDegree: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Int,
    val uvi: Float,
    val description: String,
    val location: Location
)
