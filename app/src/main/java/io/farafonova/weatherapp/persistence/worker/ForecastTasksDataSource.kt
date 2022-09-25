package io.farafonova.weatherapp.persistence.worker

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.time.Duration

private const val REFRESH_FORECASTS_PERIODICALLY_TASK = "RefreshWeatherTask"

class ForecastTasksDataSource(
    private val workManager: WorkManager,
    private val defaultSharedPreferences: SharedPreferences
) : SharedPreferences.OnSharedPreferenceChangeListener, LifecycleEventObserver {
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
        .setRequiresBatteryNotLow(true)
        .build()

    private var refreshIntervalMinutes: Duration =
        Duration.ofMinutes(defaultSharedPreferences.getString("refresh", "15")!!.toLong())

    private val _refreshWorkInfo: MutableLiveData<WorkInfo?> = MutableLiveData()
    val refreshWorkInfo: LiveData<WorkInfo?> = _refreshWorkInfo

    private var cachedWorkInfo: LiveData<List<WorkInfo>> = MutableLiveData(emptyList())

    companion object {
        private val TAG = ForecastTasksDataSource::class.qualifiedName
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun refreshForecastsPeriodically(): LiveData<List<WorkInfo>> {
        val refreshWorkRequest =
            PeriodicWorkRequestBuilder<ForecastRefreshWorker>(refreshIntervalMinutes)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            REFRESH_FORECASTS_PERIODICALLY_TASK,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshWorkRequest
        )

        return workManager
            .getWorkInfosForUniqueWorkLiveData(REFRESH_FORECASTS_PERIODICALLY_TASK)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let {
            val value = sharedPreferences.getString(key, "")
            Log.i(TAG, "Preference value has been updated to: $value")

            if (key == "refresh" && !value.isNullOrBlank()) {
                refreshIntervalMinutes = Duration.ofMinutes(value.toLong())
                workManager.cancelUniqueWork(REFRESH_FORECASTS_PERIODICALLY_TASK)

                cachedWorkInfo = refreshForecastsPeriodically()
                cachedWorkInfo.observe(ProcessLifecycleOwner.get()) {
                    _refreshWorkInfo.value = it.firstOrNull()
                    Log.i(TAG, "Updating _refreshWorkInfo from onSharedPreferenceChanged")
                }
                Log.i(TAG, "Task $REFRESH_FORECASTS_PERIODICALLY_TASK has been re-scheduled with interval $refreshIntervalMinutes.")
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.i(TAG, "onStateChanged for ${source.javaClass.name}, state: ${event.name}")
        if (source is ProcessLifecycleOwner) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    cachedWorkInfo = refreshForecastsPeriodically()
                    cachedWorkInfo.observe(ProcessLifecycleOwner.get()) {
                        _refreshWorkInfo.value = it.firstOrNull()
                        Log.i(TAG, "Updating _refreshWorkInfo from onStateChanged")
                    }
                    Log.i(TAG, "Task $REFRESH_FORECASTS_PERIODICALLY_TASK has been scheduled with interval $refreshIntervalMinutes.")
                }
                Lifecycle.Event.ON_STOP -> {
                    workManager.cancelUniqueWork(REFRESH_FORECASTS_PERIODICALLY_TASK)
                    Log.i(TAG, "Task $REFRESH_FORECASTS_PERIODICALLY_TASK has been cancelled.")
                }
                Lifecycle.Event.ON_RESUME -> {
                    defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                }
                else -> {}
            }
        }

    }
}