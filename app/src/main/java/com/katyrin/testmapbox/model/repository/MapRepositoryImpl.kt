package com.katyrin.testmapbox.model.repository

import com.katyrin.testmapbox.model.datasource.MapDataSource
import com.mapbox.geojson.Feature
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapDataSource: MapDataSource,
    private val geoPositionMapper: GeoPositionMapper,
    private val geoPointsFilter: GeoPointsFilter
) : MapRepository {

    override fun getLocations(lat: Double, lng: Double): Single<List<Feature>> =
        mapDataSource.getLocations(lat, lng)
            .map { geoPointsFilter.filter(lat, lng, it) }
            .map { geoPositionMapper.map(it) }
}