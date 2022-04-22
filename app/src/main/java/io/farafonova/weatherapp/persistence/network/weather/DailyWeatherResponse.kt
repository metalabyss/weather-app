package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DailyWeatherResponse(
    @JsonProperty("dt") val weatherTime: Int,
    @JsonProperty("wind_speed") val windSpeed: Float,
    @JsonProperty("wind_deg") val windDegree: Int,
    @JsonProperty("pressure") val pressure: Int,
    @JsonProperty("humidity") val humidity: Int,
    @JsonProperty("dew_point") val dewPoint: Float,
    @JsonProperty("uvi") val uvi: Float,
    @JsonProperty("weather") val description: List<WeatherDescription>,
    @JsonProperty("temp") val temperature: DailyTemperature,
    @JsonProperty("feels_like") val feelsLikeTemperature: DailyTemperature
)
