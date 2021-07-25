package com.katyrin.testmapbox.di.modules

import com.katyrin.testmapbox.view.MainActivity
import com.katyrin.testmapbox.view.MapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AppModule {

    @ContributesAndroidInjector
    fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    fun bindMapFragment(): MapFragment
}