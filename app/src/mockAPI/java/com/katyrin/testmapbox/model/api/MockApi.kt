package com.katyrin.testmapbox.model.api

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.mock.BehaviorDelegate
import javax.inject.Inject

class MockApi @Inject constructor(
    private val behaviorDelegate: BehaviorDelegate<Api>,
    private val geoPointsFilter: GeoPointsFilter
) : Api {

    override fun getLocationsByLatLong(lat: Double, lng: Double): Single<List<GeoPositionDTO>> {
        checkNullBaseLocation(lat, lng)
        val geoPositions: List<GeoPositionDTO> = getCircleLocations(lat, lng)
        return behaviorDelegate.returningResponse(geoPositions).getLocationsByLatLong(lat, lng)
    }

    private fun getCircleLocations(lat: Double, lng: Double): List<GeoPositionDTO> =
        geoPointsFilter.filter(lat, lng, getAllAreaLocations()).apply {
            if (isEmpty()) {
                setBaseLocation(lat, lng)
                getCircleLocations(lat, lng)
            }
        }

    private fun checkNullBaseLocation(lat: Double, lng: Double) {
        if (BASE_LATITUDE == LOCATION_NULL && BASE_LONGITUDE == LOCATION_NULL)
            setBaseLocation(lat, lng)
    }

    private fun setBaseLocation(lat: Double, lng: Double) {
        BASE_LATITUDE = lat - HALF_DEGREE
        BASE_LONGITUDE = lng - HALF_DEGREE
    }

    private fun getAllAreaLocations(): List<GeoPositionDTO> =
        mutableListOf<GeoPositionDTO>().also { geoPositions ->
            for (lat in 1..50) {
                for (lng in 1..50) {
                    geoPositions.add(
                        GeoPositionDTO(
                            BASE_LATITUDE + lat * COEFFICIENT,
                            BASE_LONGITUDE + lng * COEFFICIENT
                        )
                    )
                }
            }
        }

    private companion object {
        const val LOCATION_NULL = 0.0
        const val COEFFICIENT = 0.02
        const val HALF_DEGREE = 0.5
        var BASE_LATITUDE: Double = LOCATION_NULL
        var BASE_LONGITUDE: Double = LOCATION_NULL
    }
}