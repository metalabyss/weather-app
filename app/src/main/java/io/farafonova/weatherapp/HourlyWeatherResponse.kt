package io.farafonova.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class HourlyWeatherResponse(
    @JsonProperty("dt") val weatherTime: Int,
    @JsonProperty("temp") val temperature: Float,
    @JsonProperty("feels_like") val feelsLikeTemperature: Float,
    @JsonProperty("weather") val description: WeatherDescription
)
