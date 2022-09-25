package io.farafonova.weatherapp

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.persistence.database.WeatherDatabase
import io.farafonova.weatherapp.persistence.worker.ForecastRefreshWorkerFactory
import io.farafonova.weatherapp.persistence.worker.ForecastTasksDataSource

class WeatherApplication : Application(), Configuration.Provider {
    private val database by lazy { WeatherDatabase.getDatabase(this) }

    private val defaultSharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    val repository by lazy {
        ForecastWithLocationRepository(database.forecastDao())
    }

    private val tasksDataSource by lazy {
        ForecastTasksDataSource(
            WorkManager.getInstance(this),
            defaultSharedPreferences
        )
    }

    val refreshWorkInfo: LiveData<WorkInfo?> by lazy {
        tasksDataSource.refreshWorkInfo
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = DelegatingWorkerFactory()
        workerFactory.addFactory(ForecastRefreshWorkerFactory(repository))

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }
}