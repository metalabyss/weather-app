package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingRepository
import io.farafonova.weatherapp.ui.search.LocationSearchEntry
import io.farafonova.weatherapp.persistence.network.weather.WeatherRepository

class WeatherDatasourceManager(
    private val apiKey: String,
    private val oneCallApiBaseUrl: String,
    private val geocodingApiBaseUrl: String
) {
    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(oneCallApiBaseUrl, apiKey)
    }

    private val geocodingRepository: GeocodingRepository by lazy {
        GeocodingRepository(geocodingApiBaseUrl, apiKey)
    }

    suspend fun findLocationsByName(name: String): List<LocationSearchEntry>? {
        return geocodingRepository.getLocationByName(name)
            ?.map {
                LocationSearchEntry(it.name, it.state?: "", it.countryCode) }
    }
}