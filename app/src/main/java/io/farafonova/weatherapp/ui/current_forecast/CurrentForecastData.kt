package io.farafonova.weatherapp.ui.current_forecast

data class CurrentForecastData(
    val latitude: String,
    val longitude: String,
    val locationName: String,
    val locationCountry: String,
    val temperature: Int,
    val feelsLikeTemperature: Int,
    val forecastTime: String,
    val windSpeed: Float,
    val windDegree: Int,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Int,
    val uvi: Float,
    val description: String
)
