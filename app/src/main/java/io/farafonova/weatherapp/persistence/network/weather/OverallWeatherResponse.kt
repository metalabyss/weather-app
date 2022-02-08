package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OverallWeatherResponse(
    @JsonProperty("lat") val latitude: Float,
    @JsonProperty("lon") val longitude: Float,
    @JsonProperty("timezone_offset") val timezoneOffset: Int,
    @JsonProperty("current") val currentWeatherResponse: CurrentWeatherResponse,
    @JsonProperty("hourly") val hourlyWeather: List<HourlyWeatherResponse>,
    @JsonProperty("daily") val dailyWeather: List<DailyWeatherResponse>
)