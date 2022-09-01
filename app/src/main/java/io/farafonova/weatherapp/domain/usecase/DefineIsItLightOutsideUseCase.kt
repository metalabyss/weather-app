package io.farafonova.weatherapp.domain.usecase

import io.farafonova.weatherapp.domain.model.DailyForecast
import io.farafonova.weatherapp.persistence.WeatherDatasourceManager
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class DefineIsItLightOutsideUseCase(private val datasourceManager: WeatherDatasourceManager) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        rawInstant: Long,
        rawOffset: Int
    ): Boolean {

        val dayStartTime = Instant
            .ofEpochSecond(rawInstant)
            .atOffset(ZoneOffset.ofTotalSeconds(rawOffset))
            .truncatedTo(ChronoUnit.DAYS)

        val dayEndTime = dayStartTime.plusHours(24)

        var dailyForecast: DailyForecast? = null
        datasourceManager.getDailyForecast(
            latitude, longitude, dayStartTime.toEpochSecond(), dayEndTime.toEpochSecond()
        ).collect { dailyForecast = it }

        return dailyForecast?.let {
            rawInstant >= it.sunriseTime && rawInstant <= it.sunsetTime
        } ?: false
    }
}