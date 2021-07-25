package com.katyrin.testmapbox.model.repository

import com.mapbox.geojson.Feature
import io.reactivex.rxjava3.core.Single

interface MapRepository {
    fun getLocations(lat: Double, lng: Double): Single<List<Feature>>
}