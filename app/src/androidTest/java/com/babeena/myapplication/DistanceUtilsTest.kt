package com.babeena.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.babeena.myapplication.utils.DistanceUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DistanceUtilsTest {

    @Test
    fun calculateDistance_sameLocation_returnsZero() {
        val distance = DistanceUtils.calculateDistanceInKm(
            userLatitude = 43.6532,
            userLongitude = -79.3832,
            eventLatitude = 43.6532,
            eventLongitude = -79.3832
        )

        Assert.assertNotNull(distance)
        Assert.assertEquals(0.0, distance)
    }

    @Test
    fun calculateDistance_differentLocations_returnsPositiveDistance() {
        val distance = DistanceUtils.calculateDistanceInKm(
            userLatitude = 43.6532,
            userLongitude = -79.3832,
            eventLatitude = 43.7000,
            eventLongitude = -79.4000
        )

        Assert.assertNotNull(distance)
        Assert.assertEquals(true, distance!! > 0.0)
    }
}