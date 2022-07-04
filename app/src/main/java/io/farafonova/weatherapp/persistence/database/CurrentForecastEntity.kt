package io.farafonova.weatherapp.persistence.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "current_forecast", primaryKeys = ["lat", "lon"], foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = arrayOf("lat", "lon"),
        childColumns = arrayOf("lat", "lon"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CurrentForecastEntity(
    @ColumnInfo(name = "lat") val latitude: Float,
    @ColumnInfo(name = "lon") val longitude: Float,
    @ColumnInfo(name = "forecast_time") val forecastTime: Int,

    @ColumnInfo val temperature: Float,
    @ColumnInfo(name = "feels_like") val feelsLikeTemperature: Float,

    @ColumnInfo(name = "wind_speed") val windSpeed: Float,
    @ColumnInfo(name = "wind_degree") val windDegree: Int,
    @ColumnInfo val pressure: Int,
    @ColumnInfo val humidity: Int,
    @ColumnInfo(name = "dew_point") val dewPoint: Float,
    @ColumnInfo val uvi: Float,
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
