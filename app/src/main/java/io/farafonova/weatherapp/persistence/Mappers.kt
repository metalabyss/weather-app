package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation
import io.farafonova.weatherapp.domain.model.Country
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.DailyForecast
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.LocationResponse
import io.farafonova.weatherapp.persistence.network.weather.CurrentWeatherResponse
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.domain.model.WeatherCondition
import io.farafonova.weatherapp.persistence.database.DailyForecastEntity
import io.farafonova.weatherapp.persistence.database.HourlyForecastEntity
import io.farafonova.weatherapp.persistence.network.weather.DailyWeatherResponse
import io.farafonova.weatherapp.persistence.network.weather.HourlyWeatherResponse
import kotlin.math.roundToInt


fun Location.toLocationEntity(): LocationEntity {
    return LocationEntity(
        latitude,
        longitude,
        name,
        state,
        country.twoLetterCode(),
        timeZoneOffset,
        inFavorites
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
        name,
        state,
        Country.withCountryCode(countryCode),
        timezoneOffset,
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
        windSpeed, windDegree % 360,
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

fun HourlyForecastEntity.toHourlyForecastWithLocationModel(location: Location): HourlyForecastWithLocation {
    return HourlyForecastWithLocation(
        temperature.roundToInt(),
        feelsLikeTemperature.roundToInt(),
        (precipitationProbability * 100).roundToInt(),
        forecastTime,
        WeatherCondition.valueFrom(weatherConditionId),
        location
    )
}

fun DailyWeatherResponse.toDailyForecastEntity(
    latitude: Double,
    longitude: Double
): DailyForecastEntity {

    return DailyForecastEntity(
        latitude, longitude,
        weatherTime,

        temperature.max, temperature.min,

        temperature.morning, temperature.day,
        temperature.evening, temperature.night,

        feelsLikeTemperature.morning, feelsLikeTemperature.day,
        feelsLikeTemperature.evening, feelsLikeTemperature.night,

        windSpeed,
        windDegree % 360,
        pressure,
        humidity,
        dewPoint,
        uvi,
        precipitationProbability,
        description[0].conditionId,

        sunriseTime,
        sunsetTime
    )
}

fun DailyForecastEntity.toDailyForecastModel(): DailyForecast {
    return DailyForecast(
        morningTemperature.roundToInt(), dayTemperature.roundToInt(),
        eveningTemperature.roundToInt(), nightTemperature.roundToInt(),

        morningFeelsLikeTemperature.roundToInt(), dayFeelsLikeTemperature.roundToInt(),
        eveningFeelsLikeTemperature.roundToInt(), nightFeelsLikeTemperature.roundToInt(),

        windSpeed.roundToInt(),
        windDegree,
        pressure,
        humidity,
        dewPoint.roundToInt(),
        uvi,
        (precipitationProbability * 100).roundToInt(),

        forecastTime, sunriseTime, sunsetTime,

        WeatherCondition.valueFrom(weatherConditionId)
    )
}

fun DailyForecastEntity.toBriefDailyForecastWithLocation(location: Location): BriefDailyForecastWithLocation {
    return BriefDailyForecastWithLocation(
        maxTemperature.roundToInt(),
        minTemperature.roundToInt(),
        forecastTime,
        WeatherCondition.valueFrom(weatherConditionId),
        location
    )
}