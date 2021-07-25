package com.katyrin.testmapbox.model.datasource

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import io.reactivex.rxjava3.core.Single

interface MapDataSource {
    fun getLocations(lat: Double, lng: Double): Single<List<GeoPositionDTO>>
}