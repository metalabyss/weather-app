package io.farafonova.weatherapp.persistence.database

import android.database.sqlite.SQLiteConstraintException
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
import java.time.Instant
import java.time.temporal.ChronoUnit

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
            45.8132,
            15.977,
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
            -91.0,
            0.0,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLatitudeIsGreaterThan90_expectCreationToFail() = runTest {
        val location = LocationEntity(
            91.0,
            0.0,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLongitudeIsLessThanMinus180_expectCreationToFail() = runTest {
        val location = LocationEntity(
            0.0,
            -181.0,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenLongitudeIsGreaterThan180_expectCreationToFail() = runTest {
        val location = LocationEntity(
            0.0,
            181.0,
            "Nonexistent Place",
            "",
            false
        )
    }

    @Test
    fun whenInsertingFavoriteLocations_expectAllFavoriteLocationsToSelect() = runTest {
        val london = LocationEntity(
            51.5073,
            -0.1276,
            "London",
            "GB",
            true
        )
        val paris = LocationEntity(
            48.8589,
            2.3200,
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
            51.1282,
            71.4307,
            "Astana",
            "KZ",
            true
        )
        val nurSultan = LocationEntity(
            51.1282,
            71.4307,
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
            51.5073,
            -0.1276,
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
        val latitude = 55.0
        val longitude = 50.0
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
            15.2,
            10.0,
            6.0,
            0,
            1000,
            60,
            20.0,
            0.2,
            800,
            0,
            0
        )
        forecastDao.insertLocations(somePlace)
        forecastDao.insertCurrentForecast(forecast)

        val forecastFromDB = forecastDao.getCurrentForecastForSpecificLocation(latitude, longitude)
        assertEquals(forecast, forecastFromDB)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun whenInsertingCurrentForecastForNonStoredLocation_expectSQLiteConstraintException() = runTest {
        val latitude = 55.0
        val longitude = 50.0

        val forecast = CurrentForecastEntity(
            latitude,
            longitude,
            1618317040,
            15.2,
            10.0,
            6.0,
            0,
            1000,
            60,
            20.0,
            0.2,
            800,
            0,
            0
        )
        forecastDao.insertCurrentForecast(forecast)
    }

    @Test
    fun whenCurrentForecastForFavoriteLocationExist_expectFlowToEmitMapOfCurrentForecast() = runTest {
        val latitude = 55.0
        val longitude = 50.0
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
            15.2,
            10.0,
            6.0,
            0,
            1000,
            60,
            20.0,
            0.2,
            800,
            0,0
        )
        forecastDao.insertLocations(somePlace)
        forecastDao.insertCurrentForecast(forecast)

        val mapOfForecasts = forecastDao.getCurrentForecastForAllFavoriteLocations()
        assertEquals(mapOf(somePlace to forecast), mapOfForecasts)
    }

    @Test
    fun whenTryingToGetNonexistentLocation_expectNull() = runTest {
        val location = forecastDao.getSpecificLocation(0.0, 0.0)
        assertEquals(null, location)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun whenInsertingHourlyForecastForNonStoredLocation_expectSQLiteConstraintException() = runTest {
        val latitude = 55.0
        val longitude = 50.0

        val forecast = HourlyForecastEntity(
            latitude, longitude,
            Instant.now().epochSecond,
            15.2,
            10.0,
            0.1,
            800
        )
        forecastDao.insertHourlyForecast(forecast)
    }

    @Test
    fun whenSelectingFutureHourlyForecast_expectSuccess() = runTest {
        val latitude = 90.0
        val longitude = 60.0

        val somePlace = LocationEntity(
            latitude, longitude,
            "Some Place",
            "RU", true
        )
        forecastDao.insertLocations(somePlace)

        val now = Instant.now().epochSecond
        val currentHour = Instant.now().truncatedTo(ChronoUnit.HOURS).epochSecond
        val hourBefore = currentHour - 3600
        val hourLaterInstant = currentHour + 3600
        println("-hour $hourBefore, current hour $currentHour, +hour $hourLaterInstant, now is $now")

        val forecastLater = HourlyForecastEntity(
            latitude, longitude, hourLaterInstant,
            15.2, 10.0, 0.1, 800
        )

        val forecastCurrent = HourlyForecastEntity(
            latitude, longitude, currentHour,
            15.2, 10.0, 0.1, 800
        )

        val forecastBefore = HourlyForecastEntity(
            latitude, longitude, hourBefore,
            15.2, 10.0, 0.1, 800
        )

        forecastDao.insertHourlyForecast(forecastBefore, forecastCurrent, forecastLater)
        val resultList = forecastDao.getHourlyForecastForSpecificLocation(latitude, longitude, currentHour)

        assertEquals(listOf(forecastCurrent, forecastLater), resultList)
    }
}