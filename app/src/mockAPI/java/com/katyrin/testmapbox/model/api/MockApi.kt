package com.katyrin.testmapbox.model.api

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.mock.BehaviorDelegate

class MockApi(private val behaviorDelegate: BehaviorDelegate<Api>) : Api {

    private val geoPositions: MutableList<GeoPositionDTO> = mutableListOf()

    override fun getLocationsByLatLong(lat: Double, lng: Double): Single<List<GeoPositionDTO>> {
        var baseLatitude = lat
        var baseLongitude = lng
        for (i in 1..20) {
            baseLatitude += i * COEFFICIENT
            baseLongitude += i * COEFFICIENT
            geoPositions.add(GeoPositionDTO(baseLatitude, baseLongitude))
        }
        return behaviorDelegate.returningResponse(geoPositions).getLocationsByLatLong(lat, lng)
    }

    private companion object {
        const val COEFFICIENT = 0.001
    }
}