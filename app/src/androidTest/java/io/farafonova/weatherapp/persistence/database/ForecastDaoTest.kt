package io.farafonova.weatherapp.persistence.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class ForecastDaoTest {
    private lateinit var weatherDatabase: ExtendedWeatherDatabase
    private lateinit var forecastDao: ExtendedForecastDao

    @Before
    fun setup() {
        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExtendedWeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        forecastDao = weatherDatabase.forecastDao()
    }

    @After
    fun teardown() {
        weatherDatabase.close()
    }

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

    @Test(expected = IllegalArgumentException::class)
    fun whenLatitudeIsLessThanMinus90_expectCreationToFail() = runTest {
        val location = LocationEntity(
            -91f,
            0f,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLatitudeIsGreaterThan90_expectCreationToFail() = runTest {
        val location = LocationEntity(
            91f,
            0f,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLongitudeIsLessThanMinus180_expectCreationToFail() = runTest {
        val location = LocationEntity(
            0f,
            -181f,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLongitudeIsGreaterThan180_expectCreationToFail() = runTest {
        val location = LocationEntity(
            0f,
            181f,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test
    fun whenInsertingFavoriteLocations_expectAllFavoriteLocationsToSelect() = runTest {
        val london = LocationEntity(
            51.5073f,
            -0.1276f,
            "London",
            "GB",
            true
        )
        val paris = LocationEntity(
            48.8589f,
            2.3200f,
            "Paris",
            "FR",
            false
        )
        forecastDao.insertLocations(london, paris)
        val allFavorites = forecastDao.getAllFavoriteLocations()
        assertEquals(listOf(london), allFavorites)
    }

    @Test
    fun whenUpdatingLocation_expectUpdatedLocationToSelect() = runTest {
        val astana = LocationEntity(
            51.1282f,
            71.4307f,
            "Astana",
            "KZ",
            true
        )
        val nurSultan = LocationEntity(
            51.1282f,
            71.4307f,
            "Nur-Sultan",
            "KZ",
            true
        )
        forecastDao.insertLocations(astana)
        forecastDao.updateLocations(nurSultan)
        val locations = forecastDao.getAllLocations()
        assertEquals(listOf(nurSultan), locations)
    }

    @Test
    fun whenThereIsSuchLocation_expectTrue() = runTest {
        val london = LocationEntity(
            51.5073f,
            -0.1276f,
            "London",
            "GB",
            true
        )
        forecastDao.insertLocations(london)
        val isThereSuchLocation = forecastDao.isThereAlreadySuchLocation(
            london.latitude,
            london.longitude
        )
        assertTrue(isThereSuchLocation)
    }

    @Test
    fun whenInsertingCurrentForecastForStoredLocation_expectCurrentForecastToInsert() = runTest {
        val latitude = 55f
        val longitude = 50f
        val somePlace = LocationEntity(
            latitude,
            longitude,
            "Some Place",
            "RU",
            false
        )
        val forecast = CurrentForecastEntity(
            latitude,
            longitude,
            1618317040,
            15.2f,
            10.0f,
            6.0f,
            0,
            1000,
            60,
            20.0f,
            0.2f,
            "broken clouds"
        )
        forecastDao.insertLocations(somePlace)
        forecastDao.insertCurrentForecast(forecast)

        val forecastFromDB = forecastDao.getCurrentForecastForSpecificLocation(latitude, longitude)
        assertEquals(forecast, forecastFromDB)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun whenInsertingCurrentForecastForNonStoredLocation_expectSQLiteConstraintException() = runTest {
        val latitude = 55f
        val longitude = 50f

        val forecast = CurrentForecastEntity(
            latitude,
            longitude,
            1618317040,
            15.2f,
            10.0f,
            6.0f,
            0,
            1000,
            60,
            20.0f,
            0.2f,
            "broken clouds"
        )
        forecastDao.insertCurrentForecast(forecast)
    }

    @Test
    fun whenCurrentForecastForFavoriteLocationExist_expectFlowToEmitMapOfCurrentForecast() = runTest {
        val latitude = 55f
        val longitude = 50f
        val somePlace = LocationEntity(
            latitude,
            longitude,
            "Some Place",
            "RU",
            true
        )
        val forecast = CurrentForecastEntity(
            latitude,
            longitude,
            1618317040,
            15.2f,
            10.0f,
            6.0f,
            0,
            1000,
            60,
            20.0f,
            0.2f,
            "broken clouds"
        )
        forecastDao.insertLocations(somePlace)
        forecastDao.insertCurrentForecast(forecast)

        val flow = forecastDao.getCurrentForecastForAllFavoriteLocations()
        val forecastFromDb = flow.first()[somePlace]
        assertEquals(forecast, forecastFromDb)
    }
}