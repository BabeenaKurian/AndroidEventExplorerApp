package com.babeena.myapplication.utils

import android.location.Location

object DistanceUtils {
    fun calculateDistanceInKm(
        userLatitude: Double,
        userLongitude: Double,
        eventLatitude: Double,
        eventLongitude: Double
    ): Double? {
        if (eventLatitude == 0.0 || eventLongitude == 0.0 || userLatitude ==0.0 || userLongitude==0.0) {
            return null
        }

        val results = FloatArray(1)

        Location.distanceBetween(
            userLatitude,
            userLongitude,
            eventLatitude,
            eventLongitude,
            results
        )

        return results[0] / 1000.0
    }
}