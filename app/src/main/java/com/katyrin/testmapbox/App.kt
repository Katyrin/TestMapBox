package com.katyrin.testmapbox

import com.katyrin.testmapbox.di.DaggerAppComponent
import com.katyrin.testmapbox.utils.RxBus
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<App> =
        DaggerAppComponent
            .builder()
            .withContext(applicationContext)
            .withRxBus(RxBus())
            .build()

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private lateinit var appInstance: App
    }
}