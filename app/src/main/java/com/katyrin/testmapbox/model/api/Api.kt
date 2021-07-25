package com.katyrin.testmapbox.model.api

import com.katyrin.testmapbox.model.data.GeoPositionDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("locations/")
    fun getLocationsByLatLong(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Single<List<GeoPositionDTO>>
}