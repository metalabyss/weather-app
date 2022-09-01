package io.farafonova.weatherapp.domain.model

import androidx.annotation.DrawableRes
import io.farafonova.weatherapp.R

enum class WeatherCondition(
    private val description: String,
    @DrawableRes private val dayIcon: Int,
    @DrawableRes private val nightIcon: Int,
    @DrawableRes private val daySmallIcon: Int,
    @DrawableRes private val nightSmallIcon: Int
) {
    THUNDERSTORM_WITH_LIGHT_RAIN(
        "thunderstorm with light rain",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM_WITH_RAIN(
        "thunderstorm with rain",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM_WITH_HEAVY_RAIN(
        "thunderstorm with light rain",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    LIGHT_THUNDERSTORM(
        "light thunderstorm",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM(
        "thunderstorm",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    HEAVY_THUNDERSTORM(
        "heavy thunderstorm",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    RAGGED_THUNDERSTORM(
        "ragged thunderstorm",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM_WITH_LIGHT_DRIZZLE(
        "thunderstorm with light drizzle",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM_WITH_DRIZZLE(
        "thunderstorm with drizzle",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE(
        "thunderstorm with heavy drizzle",
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_56,
        R.drawable.ic_condition_thunderstorm_24,
        R.drawable.ic_condition_thunderstorm_24
    ),
    LIGHT_INTENSITY_DRIZZLE(
        "light intensity drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    DRIZZLE(
        "drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    HEAVY_INTENSITY_DRIZZLE(
        "heavy intensity drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    LIGHT_INTENSITY_DRIZZLE_RAIN(
        "light intensity drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    DRIZZLE_RAIN(
        "drizzle rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    HEAVY_INTENSITY_DRIZZLE_RAIN(
        "heavy intensity drizzle rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    SHOWER_RAIN_AND_DRIZZLE(
        "shower rain and drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    HEAVY_SHOWER_RAIN_AND_DRIZZLE(
        "heavy shower rain and drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    SHOWER_DRIZZLE(
        "shower drizzle",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    LIGHT_RAIN(
        "light rain",
        R.drawable.ic_condition_rain_day_56,
        R.drawable.ic_condition_rain_night_56,
        R.drawable.ic_condition_rain_day_24,
        R.drawable.ic_condition_rain_night_24
    ),
    MODERATE_RAIN(
        "moderate rain",
        R.drawable.ic_condition_rain_day_56,
        R.drawable.ic_condition_rain_night_56,
        R.drawable.ic_condition_rain_day_24,
        R.drawable.ic_condition_rain_night_24
    ),
    HEAVY_INTENSITY_RAIN(
        "heavy intensity rain",
        R.drawable.ic_condition_rain_day_56,
        R.drawable.ic_condition_rain_night_56,
        R.drawable.ic_condition_rain_day_24,
        R.drawable.ic_condition_rain_night_24
    ),
    VERY_HEAVY_RAIN(
        "very heavy rain",
        R.drawable.ic_condition_rain_day_56,
        R.drawable.ic_condition_rain_night_56,
        R.drawable.ic_condition_rain_day_24,
        R.drawable.ic_condition_rain_night_24
    ),
    EXTREME_RAIN(
        "extreme rain",
        R.drawable.ic_condition_rain_day_56,
        R.drawable.ic_condition_rain_night_56,
        R.drawable.ic_condition_rain_day_24,
        R.drawable.ic_condition_rain_night_24
    ),
    FREEZING_RAIN(
        "freezing rain",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    LIGHT_INTENSITY_SHOWER_RAIN(
        "light intensity shower rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    SHOWER_RAIN(
        "shower rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    HEAVY_INTENSITY_SHOWER_RAIN(
        "heavy intensity shower rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    RAGGED_SHOWER_RAIN(
        "ragged shower rain",
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_56,
        R.drawable.ic_condition_shower_rain_24,
        R.drawable.ic_condition_shower_rain_24
    ),
    LIGHT_SNOW(
        "light snow",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    SNOW(
        "snow",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    HEAVY_SNOW(
        "heavy snow",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    SLEET(
        "sleet",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    LIGHT_SHOWER_SLEET(
        "light shower sleet",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    SHOWER_SLEET(
        "shower sleet",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    LIGHT_RAIN_AND_SNOW(
        "light rain and snow",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    RAIN_AND_SNOW(
        "rain and snow",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    LIGHT_SHOWER_SNOW(
        "freezing rain",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    SHOWER_SNOW(
        "freezing rain",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    HEAVY_SHOWER_SNOW(
        "freezing rain",
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_56,
        R.drawable.ic_condition_snow_24,
        R.drawable.ic_condition_snow_24
    ),
    MIST(
        "mist",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    SMOKE(
        "smoke",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    HAZE(
        "haze",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    DUST_SWIRL(
        "sand/dust whirls",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    FOG(
        "fog",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    SAND(
        "sand",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    DUST(
        "dust",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    ASH(
        "volcanic ash",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    SQUALL(
        "squalls",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    TORNADO(
        "tornado",
        R.drawable.ic_condition_mist_day_56,
        R.drawable.ic_condition_mist_night_56,
        R.drawable.ic_condition_mist_day_24,
        R.drawable.ic_condition_mist_night_24
    ),
    CLEAR(
        "clear sky",
        R.drawable.ic_condition_clear_sky_day_56,
        R.drawable.ic_condition_clear_sky_night_56,
        R.drawable.ic_condition_clear_sky_day_24,
        R.drawable.ic_condition_clear_sky_night_24
    ),
    FEW_CLOUDS(
        "few clouds: 11-25%",
        R.drawable.ic_condition_few_clouds_day_56,
        R.drawable.ic_condition_few_clouds_night_56,
        R.drawable.ic_condition_few_clouds_day_24,
        R.drawable.ic_condition_few_clouds_night_24
    ),
    SCATTERED_CLOUDS(
        "scattered clouds: 25-50%",
        R.drawable.ic_condition_scattered_clouds_56,
        R.drawable.ic_condition_scattered_clouds_56,
        R.drawable.ic_condition_scattered_clouds_24,
        R.drawable.ic_condition_scattered_clouds_24
    ),
    BROKEN_CLOUDS(
        "broken clouds: 51-84%",
        R.drawable.ic_condition_broken_clouds_56,
        R.drawable.ic_condition_broken_clouds_56,
        R.drawable.ic_condition_broken_clouds_24,
        R.drawable.ic_condition_broken_clouds_24
    ),
    OVERCAST_CLOUDS(
        "overcast clouds: 85-100%",
        R.drawable.ic_condition_broken_clouds_56,
        R.drawable.ic_condition_broken_clouds_56,
        R.drawable.ic_condition_broken_clouds_24,
        R.drawable.ic_condition_broken_clouds_24
    );


    companion object {
        @JvmStatic
        fun valueFrom(code: Int): WeatherCondition {
            return when(code) {
                // Group 2xx: Thunderstorm
                200 -> THUNDERSTORM_WITH_LIGHT_RAIN
                201 -> THUNDERSTORM_WITH_RAIN
                202 -> THUNDERSTORM_WITH_HEAVY_RAIN
                210 -> LIGHT_THUNDERSTORM
                211 -> THUNDERSTORM
                212 -> HEAVY_THUNDERSTORM
                221 -> RAGGED_THUNDERSTORM
                230 -> THUNDERSTORM_WITH_LIGHT_DRIZZLE
                231 -> THUNDERSTORM_WITH_DRIZZLE
                232 -> THUNDERSTORM_WITH_HEAVY_DRIZZLE

                // Group 3xx: Drizzle
                300 -> LIGHT_INTENSITY_DRIZZLE
                301 -> DRIZZLE
                302 -> HEAVY_INTENSITY_DRIZZLE
                310 -> LIGHT_INTENSITY_DRIZZLE_RAIN
                311 -> DRIZZLE_RAIN
                312 -> HEAVY_INTENSITY_DRIZZLE_RAIN
                313 -> SHOWER_RAIN_AND_DRIZZLE
                314 -> HEAVY_SHOWER_RAIN_AND_DRIZZLE
                321 -> SHOWER_DRIZZLE

                // Group 5xx: Rain
                500 -> LIGHT_RAIN
                501 -> MODERATE_RAIN
                502 -> HEAVY_INTENSITY_RAIN
                503 -> VERY_HEAVY_RAIN
                504 -> EXTREME_RAIN
                511 -> FREEZING_RAIN
                520 -> LIGHT_INTENSITY_SHOWER_RAIN
                521 -> SHOWER_RAIN
                522 -> HEAVY_INTENSITY_SHOWER_RAIN
                531 -> RAGGED_SHOWER_RAIN

                // Group 6xx: Snow
                600 -> LIGHT_SNOW
                601 -> SNOW
                602 -> HEAVY_SNOW
                611 -> SLEET
                612 -> LIGHT_SHOWER_SLEET
                613 -> SHOWER_SLEET
                615 -> LIGHT_RAIN_AND_SNOW
                616 -> RAIN_AND_SNOW
                620 -> LIGHT_SHOWER_SNOW
                621 -> SHOWER_SNOW
                622 -> HEAVY_SHOWER_SNOW

                // Group 7xx: Atmosphere
                701 -> MIST
                711 -> SMOKE
                721 -> HAZE
                731 -> DUST_SWIRL
                741 -> FOG
                751 -> SAND
                761 -> DUST
                762 -> ASH
                771 -> SQUALL
                781 -> TORNADO

                // Group 800: Clear
                800 -> CLEAR

                // Group 80x: Clouds
                801 -> FEW_CLOUDS
                802 -> SCATTERED_CLOUDS
                803 -> BROKEN_CLOUDS
                804 -> OVERCAST_CLOUDS

                else -> CLEAR
            }
        }
    }

    fun dayIcon() = this.dayIcon
    fun nightIcon() = this.nightIcon

    fun daySmallIcon() = this.daySmallIcon
    fun nightSmallIcon() = this.nightSmallIcon

    fun description() = this.description
}