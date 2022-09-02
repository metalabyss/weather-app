package io.farafonova.weatherapp.domain.model

data class BriefDailyForecastWithLocation(
    val maxTemperature: Int,
    val minTemperature: Int,
    val forecastTime: Long,
    val weatherCondition: WeatherCondition,
    val location: Location
)
