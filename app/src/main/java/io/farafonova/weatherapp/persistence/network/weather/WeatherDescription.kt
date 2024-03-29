package io.farafonova.weatherapp.persistence.network.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherDescription(
    @JsonProperty("id") val conditionId: Int,
    @JsonProperty("description") val description: String
)
