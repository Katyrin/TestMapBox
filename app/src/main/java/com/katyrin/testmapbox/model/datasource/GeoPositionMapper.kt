package com.katyrin.testmapbox.model.datasource

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import javax.inject.Inject

class GeoPositionMapper @Inject constructor() {

    fun map(geoPositions: List<GeoPositionDTO>): List<Feature> {
        val listFeature: MutableList<Feature> = mutableListOf()
        for (geoPoint in geoPositions) {
            val lng: Double = geoPoint.longitude ?: break
            val lat: Double = geoPoint.latitude ?: break
            listFeature.add(Feature.fromGeometry(Point.fromLngLat(lng, lat)))
        }
        return listFeature.toList()
    }
}