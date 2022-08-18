package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.domain.model.Country
import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.LocationResponse
import io.farafonova.weatherapp.persistence.network.weather.CurrentWeatherResponse
import io.farafonova.weatherapp.domain.model.Location


fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        latitude,
        longitude,
        locationName = name,
        countryCode = country.twoLetterCode(),
        inFavorites = inFavorites
    )
}

fun LocationResponse.toLocationModel(): Location {
    return Location(
        latitude.toFloat(),
        longitude.toFloat(),
        name,
        state ?: "",
        Country.withCountryCode(countryCode)
    )
}

fun LocationEntity.toLocationModel(): Location {
    return Location(
        latitude,
        longitude,
        locationName,
        "",
        Country.withCountryCode(countryCode),
        inFavorites
    )
}

fun CurrentWeatherResponse.toCurrentForecastEntity(latitude: Float, longitude: Float): CurrentForecastEntity {
    return CurrentForecastEntity(
        latitude, longitude,
        currentTime,
        temperature, feelsLikeTemperature,
        windSpeed, windDegree,
        pressure, humidity,
        dewPoint, uvi,
        description[0].description
    )
}