package io.farafonova.weatherapp.domain.model


data class CurrentForecastWithLocation(
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val forecastTime: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val windSpeed: Int,
    val windDegree: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Int,
    val uvi: Double,
    val weatherCondition: WeatherCondition,
    val location: Location
)
