package com.katyrin.testmapbox.model.api

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import javax.inject.Inject
import kotlin.math.*

class GeoPointsFilter @Inject constructor() {

    fun filter(
        lat: Double,
        lng: Double,
        geoPoints: List<GeoPositionDTO>
    ): List<GeoPositionDTO> =
        mutableListOf<GeoPositionDTO>().apply {
            val d2r = Math.PI / 180
            for (geoPoint in geoPoints) {
                try {
                    val dlong: Double = (geoPoint.longitude - lng) * d2r
                    val dlat: Double = (geoPoint.latitude - lat) * d2r
                    val a = (sin(dlat / 2.0).pow(2.0)
                            + (cos(lat * d2r)
                            * cos(geoPoint.latitude * d2r)
                            * sin(dlong / 2.0).pow(2.0)))
                    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
                    val distance = 6367 * c
                    if (distance < 10) add(geoPoint)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
}