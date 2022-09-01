package io.farafonova.weatherapp.domain.model

data class DailyForecast(
    val morningTemperature: Int,
    val dayTemperature: Int,
    val eveningTemperature: Int,
    val nightTemperature: Int,

    val morningFeelsLikeTemperature: Int,
    val dayFeelsLikeTemperature: Int,
    val eveningFeelsLikeTemperature: Int,
    val nightFeelsLikeTemperature: Int,

    val windSpeed: Int,
    val windDegree: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Int,
    val uvi: Double,
    val precipitationProbability: Int,

    val forecastTime: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,

    val weatherCondition: WeatherCondition
)