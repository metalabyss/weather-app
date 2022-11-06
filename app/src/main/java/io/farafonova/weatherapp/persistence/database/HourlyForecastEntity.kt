package io.farafonova.weatherapp.persistence.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "hourly_forecast", primaryKeys = ["lat", "lon", "forecast_time"],
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = arrayOf("lat", "lon"),
        childColumns = arrayOf("lat", "lon"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class HourlyForecastEntity(
    @ColumnInfo(name = "lat") val latitude: Double,
    @ColumnInfo(name = "lon") val longitude: Double,
    @ColumnInfo(name = "forecast_time") val forecastTime: Long,

    @ColumnInfo val temperature: Double,
    @ColumnInfo(name = "feels_like") val feelsLikeTemperature: Double,

    @ColumnInfo(name = "wind_speed", defaultValue = "0.0") val windSpeed: Double,
    @ColumnInfo(name = "wind_degree", defaultValue = "0") val windDegree: Int,
    @ColumnInfo(defaultValue = "1000") val pressure: Int,
    @ColumnInfo(defaultValue = "50") val humidity: Int,
    @ColumnInfo(name = "dew_point", defaultValue = "0.0") val dewPoint: Double,
    @ColumnInfo(defaultValue = "0.0") val uvi: Double,

    @ColumnInfo("pop") val precipitationProbability: Double,
    @ColumnInfo("condition_id", defaultValue = "800") val weatherConditionId: Int
) {
    init {
        require(latitude >= -90.0 && latitude <= 90.0)
        require(longitude >= -180.0 && longitude <= 180.0)
        require(windSpeed >= 0.0)
        require(windDegree in 0..359)
        require(pressure in 870..1085)
        require(humidity in 0..100)
        require(uvi >= 0.0)
        require(precipitationProbability in 0.0..1.0)
    }
}
