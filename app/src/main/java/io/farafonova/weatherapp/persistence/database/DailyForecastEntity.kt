package io.farafonova.weatherapp.persistence.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "daily_forecast",
    primaryKeys = ["lat", "lon", "forecast_time"],
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = arrayOf("lat", "lon"),
        childColumns = arrayOf("lat", "lon"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DailyForecastEntity(
    @ColumnInfo(name = "lat") val latitude: Float,
    @ColumnInfo(name = "lon") val longitude: Float,
    @ColumnInfo(name = "forecast_time") val forecastTime: Int,

    @ColumnInfo(name = "temp_mor") val morningTemperature: Float,
    @ColumnInfo(name = "temp_day") val dayTemperature: Float,
    @ColumnInfo(name = "temp_eve") val eveningTemperature: Float,
    @ColumnInfo(name = "temp_night") val nightTemperature: Float,

    @ColumnInfo(name = "feels_like_mor") val morningFeelsLikeTemperature: Float,
    @ColumnInfo(name = "feels_like_day") val dayFeelsLikeTemperature: Float,
    @ColumnInfo(name = "feels_like_eve") val eveningFeelsLikeTemperature: Float,
    @ColumnInfo(name = "feels_like_night") val nightFeelsLikeTemperature: Float,

    @ColumnInfo(name = "wind_speed") val windSpeed: Float,
    @ColumnInfo(name = "wind_degree") val windDegree: Int,
    @ColumnInfo val pressure: Int,
    @ColumnInfo val humidity: Int,
    @ColumnInfo(name = "dew_point") val dewPoint: Float,
    @ColumnInfo val uvi: Float,
    @ColumnInfo val description: String
)
