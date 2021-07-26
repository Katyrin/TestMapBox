package com.katyrin.testmapbox.model.datasource

import com.katyrin.testmapbox.model.api.Api
import com.katyrin.testmapbox.model.data.GeoPositionDTO
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MapDataSourceImpl @Inject constructor(
    private val api: Api
) : MapDataSource {
    override fun getLocations(lat: Double, lng: Double): Single<List<GeoPositionDTO>> =
        api.getLocationsByLatLong(lat, lng)
            .subscribeOn(Schedulers.io())

}