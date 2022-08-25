package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class HourlyWeatherResponse(
    @JsonProperty("dt") val weatherTime: Long,
    @JsonProperty("temp") val temperature: Double,
    @JsonProperty("feels_like") val feelsLikeTemperature: Double,
    @JsonProperty("pop") val precipitationProbability: Double,
    @JsonProperty("weather") val description: List<WeatherDescription>
)
