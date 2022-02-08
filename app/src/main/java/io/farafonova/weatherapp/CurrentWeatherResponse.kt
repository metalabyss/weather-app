package io.farafonova.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentWeatherResponse(
    @JsonProperty("dt") val currentTime: Int,
    @JsonProperty("temp") val temperature: Float,
    @JsonProperty("feels_like") val feelsLikeTemperature: Float,
    @JsonProperty("wind_speed") val windSpeed: Float,
    @JsonProperty("wind_deg") val windDegree: Int,
    @JsonProperty("pressure") val pressure: Int,
    @JsonProperty("humidity") val humidity: Int,
    @JsonProperty("dew_point") val dewPoint: Float,
    @JsonProperty("uvi") val uvi: Float,
    @JsonProperty("weather") val description: WeatherDescription
)
