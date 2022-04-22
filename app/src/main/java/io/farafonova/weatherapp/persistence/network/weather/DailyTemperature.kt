package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonProperty

data class DailyTemperature(
    @JsonProperty("min") val min: Float,
    @JsonProperty("max") val max: Float,
    @JsonProperty("morn") val morning: Float,
    @JsonProperty("eve") val evening: Float,
    @JsonProperty("day") val day: Float,
    @JsonProperty("night") val night: Float
)
