package io.farafonova.weatherapp.persistence.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "location", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    @ColumnInfo(name = "lat") val latitude: Float,
    @ColumnInfo(name = "lon") val longitude: Float,
    @ColumnInfo(name = "name") val locationName: String,
    @ColumnInfo(name = "timezone_offset") val timeZoneOffset: Int,
    @ColumnInfo(name = "in_favorites", defaultValue = "FALSE") var inFavorites: Boolean
)
