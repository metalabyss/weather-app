package io.farafonova.weatherapp.persistence.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [
        LocationEntity::class,
        CurrentForecastEntity::class,
        DailyForecastEntity::class,
        HourlyForecastEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration (from = 3, to = 4, spec = WeatherDatabase.MyAutoMigrationFrom3To4::class)
    ]
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    @DeleteColumn(tableName = "current_forecast", columnName = "description")
    @DeleteColumn(tableName = "daily_forecast", columnName = "description")
    @RenameColumn(
        tableName = "hourly_forecast",
        fromColumnName = "precipitationProbability",
        toColumnName = "pop"
    )
    class MyAutoMigrationFrom3To4 : AutoMigrationSpec
}