package io.farafonova.weatherapp.persistence.network.geocoding

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/* Location is accurate to 6 digits after the decimal point due to API design. */

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    @JsonProperty("name") val name: String,
    @JsonProperty("lat") val latitude: String,
    @JsonProperty("lon") val longitude: String,
    @JsonProperty("state") val state: String?,
    @JsonProperty("country") val countryCode: String
)
