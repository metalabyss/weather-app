package io.farafonova.weatherapp.domain.model

data class HourlyForecast(
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val precipitationProbability: Int,
    val forecastTime: String
)
