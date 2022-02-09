package io.farafonova.weatherapp

import android.app.Application
import io.farafonova.weatherapp.persistence.WeatherDatasourceManager
import io.farafonova.weatherapp.persistence.database.WeatherDatabase

class WeatherApplication : Application() {
    private val apiKey by lazy { applicationContext.getString(R.string.api_key) }
    private val oneCallApiBaseUrl by lazy { applicationContext.getString(R.string.one_call_api_base_url) }
    private val geocodingApiBaseUrl by lazy { applicationContext.getString(R.string.geocoding_api_base_url) }

    private val database by lazy { WeatherDatabase.getDatabase(this) }

    val datasourceManager by lazy {
        WeatherDatasourceManager(
            database.forecastDao(),
            apiKey,
            oneCallApiBaseUrl,
            geocodingApiBaseUrl
        )
    }
}