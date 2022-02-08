package io.farafonova.weatherapp

import com.fasterxml.jackson.annotation.JsonProperty

data class DailyTemperature(
    @JsonProperty val min: Float,
    @JsonProperty val max: Float,
    @JsonProperty("morn") val morning: Float,
    @JsonProperty("eve") val evening: Float,
    @JsonProperty val day: Float,
    @JsonProperty val night: Float
)
