package io.farafonova.weatherapp.persistence.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "location", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    @ColumnInfo(name = "lat") val latitude: Double,
    @ColumnInfo(name = "lon") val longitude: Double,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "state", defaultValue = "") val state: String,
    @ColumnInfo(name = "country") val countryCode: String,
    @ColumnInfo(name = "timezone_offset", defaultValue = "0") val timezoneOffset: Int,
    @ColumnInfo(name = "in_favorites", defaultValue = "FALSE") var inFavorites: Boolean
) {
    init {
        require(latitude >= -90.0 && latitude <= 90.0)
        require(longitude >= -180.0 && longitude <= 180.0)
    }
}
