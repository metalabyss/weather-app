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
    @ColumnInfo(name = "lat") val latitude: Double,
    @ColumnInfo(name = "lon") val longitude: Double,
    @ColumnInfo(name = "forecast_time") val forecastTime: Long,

    @ColumnInfo(name = "temp_mor") val morningTemperature: Double,
    @ColumnInfo(name = "temp_day") val dayTemperature: Double,
    @ColumnInfo(name = "temp_eve") val eveningTemperature: Double,
    @ColumnInfo(name = "temp_night") val nightTemperature: Double,

    @ColumnInfo(name = "feels_like_mor") val morningFeelsLikeTemperature: Double,
    @ColumnInfo(name = "feels_like_day") val dayFeelsLikeTemperature: Double,
    @ColumnInfo(name = "feels_like_eve") val eveningFeelsLikeTemperature: Double,
    @ColumnInfo(name = "feels_like_night") val nightFeelsLikeTemperature: Double,

    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "wind_degree") val windDegree: Int,
    @ColumnInfo val pressure: Int,
    @ColumnInfo val humidity: Int,
    @ColumnInfo(name = "dew_point") val dewPoint: Double,
    @ColumnInfo val uvi: Double,
    @ColumnInfo val description: String
) {
    init {
        require(latitude >= -90.0 && latitude <= 90.0)
        require(longitude >= -180.0 && longitude <= 180.0)
        require(windSpeed >= 0.0)
        require(windDegree in 0..359)
        require(pressure in 870..1085)
        require(humidity in 0..100)
        require(uvi >= 0.0)
    }
}
