package io.farafonova.weatherapp.domain.model

data class HourlyForecastWithLocation(
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val precipitationProbability: Int,
    val forecastTime: Long,
    val weatherCondition: WeatherCondition,
    val location: Location
)
