package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonProperty

data class DailyTemperature(
    @JsonProperty("min") val min: Double,
    @JsonProperty("max") val max: Double,
    @JsonProperty("morn") val morning: Double,
    @JsonProperty("eve") val evening: Double,
    @JsonProperty("day") val day: Double,
    @JsonProperty("night") val night: Double
)
