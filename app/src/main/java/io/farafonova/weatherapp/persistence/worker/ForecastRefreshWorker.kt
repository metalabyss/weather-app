package io.farafonova.weatherapp.persistence.worker

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class ForecastRefreshWorker(
    private val repository: ForecastWithLocationRepository,
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            repository.refreshAllFavoriteForecastsFromRemote()

            val now = Instant.now()
            val key = applicationContext.getString(R.string.prefs_key_last_sync_time)

            val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            preferences.edit(commit = true) { putLong(key, now.epochSecond) }

            Log.i(
                ForecastRefreshWorker::class.qualifiedName,
                "Forecast data has been updated at $now."
            )
            Result.success()
        } catch (throwable: Throwable) {
            if (throwable.message == null) {
                Log.e(ForecastRefreshWorker::class.qualifiedName, throwable.toString())
            } else {
                Log.e(ForecastRefreshWorker::class.qualifiedName, throwable.message!!)
            }
            Result.failure()
        }
    }
}

class ForecastRefreshWorkerFactory(private val repository: ForecastWithLocationRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ForecastRefreshWorker::class.java.name -> ForecastRefreshWorker(
                repository,
                appContext,
                workerParameters
            )
            else -> null
        }
    }
}
