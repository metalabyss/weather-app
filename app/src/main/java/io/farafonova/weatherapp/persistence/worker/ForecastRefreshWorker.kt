package io.farafonova.weatherapp.persistence.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastRefreshWorker(
    private val repository: ForecastWithLocationRepository,
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            repository.refreshAllFavoriteForecastsFromRemote()
            Log.i(
                ForecastRefreshWorker::class.qualifiedName,
                "Forecast data has been updated."
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