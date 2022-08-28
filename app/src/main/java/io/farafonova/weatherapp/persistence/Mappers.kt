package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.Country
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.HourlyForecast
import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.LocationResponse
import io.farafonova.weatherapp.persistence.network.weather.CurrentWeatherResponse
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.domain.model.WeatherCondition
import io.farafonova.weatherapp.persistence.database.HourlyForecastEntity
import io.farafonova.weatherapp.persistence.network.weather.HourlyWeatherResponse
import kotlin.math.roundToInt


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
        latitude.toDouble(),
        longitude.toDouble(),
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

fun CurrentWeatherResponse.toCurrentForecastEntity(
    latitude: Double,
    longitude: Double
): CurrentForecastEntity {
    return CurrentForecastEntity(
        latitude, longitude,
        currentTime,
        temperature, feelsLikeTemperature,
        windSpeed, windDegree,
        pressure, humidity,
        dewPoint, uvi,
        description[0].conditionId,
        sunriseTime,
        sunsetTime
    )
}

fun CurrentForecastEntity.toCurrentForecastWithLocation(
    location: Location
): CurrentForecastWithLocation {

    return CurrentForecastWithLocation(
        temperature.roundToInt(),
        feelsLikeTemperature.roundToInt(),
        forecastTime,
        sunriseTime,
        sunsetTime,
        windSpeed.roundToInt(),
        windDegree,
        pressure,
        humidity,
        dewPoint.roundToInt(),
        uvi,
        WeatherCondition.valueFrom(weatherConditionId),
        location
    )
}

fun CurrentForecastEntity.toBriefCurrentForecastWithLocation(
    location: Location
): BriefCurrentForecastWithLocation = BriefCurrentForecastWithLocation(
    temperature.roundToInt(),
    location
)

fun HourlyWeatherResponse.toHourlyForecastEntity(
    latitude: Double,
    longitude: Double

): HourlyForecastEntity = HourlyForecastEntity(
    latitude, longitude,
    weatherTime,
    temperature, feelsLikeTemperature,
    precipitationProbability,
    description[0].conditionId
)

fun HourlyForecastEntity.toHourlyForecastModel(): HourlyForecast {
    return HourlyForecast(
        temperature.roundToInt(),
        feelsLikeTemperature.roundToInt(),
        (precipitationProbability * 100).roundToInt(),
        forecastTime,
        WeatherCondition.valueFrom(weatherConditionId)
    )
}