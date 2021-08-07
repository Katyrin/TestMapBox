package com.katyrin.testmapbox.di.modules

import com.katyrin.testmapbox.model.api.Api
import com.katyrin.testmapbox.model.api.MockApi
import com.katyrin.testmapbox.model.api.GeoPointsFilter
import com.katyrin.testmapbox.utils.BASE_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideBehavior(): NetworkBehavior = NetworkBehavior.create()

    @Provides
    @Singleton
    fun provideMockRetrofit(retrofit: Retrofit, behavior: NetworkBehavior): MockRetrofit =
        MockRetrofit.Builder(retrofit).networkBehavior(behavior).build()

    @Provides
    @Singleton
    fun provideDelegate(mockRetrofit: MockRetrofit): BehaviorDelegate<Api> =
        mockRetrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun provideMockCurrencyApi(
        delegate: BehaviorDelegate<Api>,
        geoPointsFilter: GeoPointsFilter
    ): MockApi = MockApi(delegate, geoPointsFilter)
}