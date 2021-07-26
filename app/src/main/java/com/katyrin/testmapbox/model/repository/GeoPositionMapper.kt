package com.katyrin.testmapbox.model.repository

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import javax.inject.Inject

class GeoPositionMapper @Inject constructor() {

    fun map(geoPositions: List<GeoPositionDTO>): List<Feature> {
        val listFeature: MutableList<Feature> = mutableListOf()
        for (geoPoint in geoPositions) {
            val lng: Double = geoPoint.longitude
            val lat: Double = geoPoint.latitude
            listFeature.add(Feature.fromGeometry(Point.fromLngLat(lng, lat)))
        }
        return listFeature.toList()
    }
}