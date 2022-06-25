package io.farafonova.weatherapp.persistence.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ForecastDaoTest {
    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var forecastDao: ForecastDao

    @Before
    fun setup() {
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        forecastDao = weatherDatabase.forecastDao()
    }

    @After
    fun teardown() {
        weatherDatabase.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenInsertingValidLocation_expectLocationToInsert() = runTest {
        val location = LocationEntity(
            45.8132f,
            15.9772f,
            "Zagreb",
            "HR",
            false
        )
        forecastDao.insertLocations(location)
        val allLocations = forecastDao.getAllLocations()
        assertEquals(listOf(location), allLocations)
    }
}