package io.farafonova.weatherapp.domain.model

import kotlin.math.roundToInt

enum class WindDirection(private val abbreviation: String) {
    NORTH("N"),
    NORTHEAST("NE"),
    EAST("E"),
    SOUTHEAST("SE"),
    SOUTH("S"),
    SOUTHWEST("SW"),
    WEST("W"),
    NORTHWEST("NW");

    companion object {
        private val DIRECTIONS_NUMBER = values().size
        private val DEGREES_PER_DIRECTION = 360 / DIRECTIONS_NUMBER

        @JvmStatic
        fun valueFrom(degree: Int): WindDirection {
            if (degree > 359 || degree < 0) throw IllegalArgumentException("Degree is a number between 0 and 359 (inclusive).")

            val directionNumber =
                (degree.toFloat() / DEGREES_PER_DIRECTION.toFloat()).roundToInt() % DIRECTIONS_NUMBER

            return values()[directionNumber]
        }
    }

    fun abbreviation() = this.abbreviation
}