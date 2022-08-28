package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrentWeatherResponse(
    @JsonProperty("dt") val currentTime: Long,
    @JsonProperty("sunrise") val sunriseTime: Long,
    @JsonProperty("sunset") val sunsetTime: Long,
    @JsonProperty("temp") val temperature: Double,
    @JsonProperty("feels_like") val feelsLikeTemperature: Double,
    @JsonProperty("wind_speed") val windSpeed: Double,
    @JsonProperty("wind_deg") val windDegree: Int,
    @JsonProperty("pressure") val pressure: Int,
    @JsonProperty("humidity") val humidity: Int,
    @JsonProperty("dew_point") val dewPoint: Double,
    @JsonProperty("uvi") val uvi: Double,
    @JsonProperty("weather") val description: List<WeatherDescription>
)
