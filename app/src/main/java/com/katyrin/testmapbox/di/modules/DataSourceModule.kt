package com.katyrin.testmapbox.di.modules

import com.katyrin.testmapbox.model.datasource.MapDataSource
import com.katyrin.testmapbox.model.datasource.MapDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataSourceModule {

    @Binds
    @Singleton
    fun bindMapDataSource(mapDataSourceImpl: MapDataSourceImpl): MapDataSource
}