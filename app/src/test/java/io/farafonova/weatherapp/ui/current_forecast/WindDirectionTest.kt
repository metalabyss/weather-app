package io.farafonova.weatherapp.ui.current_forecast

import io.farafonova.weatherapp.domain.model.WindDirection
import org.junit.Assert.assertEquals
import org.junit.Test

internal class WindDirectionTest {

    @Test
    fun whenDegreeIs359_expectWindDirectionIsNorth() {
        assertEquals(WindDirection.NORTH, WindDirection.valueFrom(359))
    }

    @Test
    fun whenDegreeIsZero_expectWindDirectionIsNorth() {
        assertEquals(WindDirection.NORTH, WindDirection.valueFrom(0))
    }

    @Test
    fun whenDegreeIs22_expectWindDirectionIsNorth() {
        assertEquals(WindDirection.NORTH, WindDirection.valueFrom(22))
    }

    @Test
    fun whenDegreeIs23_expectWindDirectionIsNortheast() {
        assertEquals(WindDirection.NORTHEAST, WindDirection.valueFrom(23))
    }

    @Test
    fun whenDegreeIs45_expectWindDirectionIsNortheast() {
        assertEquals(WindDirection.NORTHEAST, WindDirection.valueFrom(45))
    }

    @Test
    fun whenDegreeIs90_expectWindDirectionIsEast() {
        assertEquals(WindDirection.EAST, WindDirection.valueFrom(90))
    }

    @Test
    fun whenDegreeIs135_expectWindDirectionIsSoutheast() {
        assertEquals(WindDirection.SOUTHEAST, WindDirection.valueFrom(135))
    }

    @Test
    fun whenDegreeIs180_expectWindDirectionIsSouth() {
        assertEquals(WindDirection.SOUTH, WindDirection.valueFrom(180))
    }

    @Test
    fun whenDegreeIs225_expectWindDirectionIsSouthwest() {
        assertEquals(WindDirection.SOUTHWEST, WindDirection.valueFrom(225))
    }

    @Test
    fun whenDegreeIs270_expectWindDirectionIsWest() {
        assertEquals(WindDirection.WEST, WindDirection.valueFrom(270))
    }

    @Test
    fun whenDegreeIs315_expectWindDirectionIsNorthwest() {
        assertEquals(WindDirection.NORTHWEST, WindDirection.valueFrom(315))
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenDegreeIsGreaterThan359_expectIllegalArgumentException() {
        WindDirection.valueFrom(360)
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenDegreeIsNegative_expectIllegalArgumentException() {
        WindDirection.valueFrom(-1)
    }
}