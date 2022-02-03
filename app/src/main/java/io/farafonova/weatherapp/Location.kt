package io.farafonova.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    @JsonProperty("name") val name: String,
    @JsonProperty("lat") val latitude: String,
    @JsonProperty("lon") val longitude: String,
    @JsonProperty("country") val countryCode: String
)
