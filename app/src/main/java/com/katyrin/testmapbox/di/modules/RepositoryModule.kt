package com.katyrin.testmapbox.di.modules

import com.katyrin.testmapbox.model.repository.MapRepository
import com.katyrin.testmapbox.model.repository.MapRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindMapRepository(mapRepositoryImpl: MapRepositoryImpl): MapRepository
}