package io.farafonova.weatherapp.domain.usecase

import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository

class DefineIsItLightOutsideUseCase(private val datasourceManager: ForecastWithLocationRepository) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        rawInstant: Long,
        rawOffset: Int
    ): Boolean {

        val sunriseAndSunset = datasourceManager
            .getSunriseAndSunsetInLocationOnParticularDay(
                latitude, longitude, rawInstant, rawOffset
            )
        val sunrise = sunriseAndSunset?.first
        val sunset = sunriseAndSunset?.second

        return sunrise != null && sunset != null
                && rawInstant >= sunrise && rawInstant <= sunset
    }
}