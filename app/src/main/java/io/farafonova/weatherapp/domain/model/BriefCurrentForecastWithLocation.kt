package io.farafonova.weatherapp.domain.model

data class BriefCurrentForecastWithLocation(
    val temperature: Int,
    val feelsLikeTemperature: Int,

    val forecastTime: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,

    val weatherCondition: WeatherCondition,
    val location: Location
)
