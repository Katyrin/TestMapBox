package com.katyrin.testmapbox.di

import android.content.Context
import com.katyrin.testmapbox.App
import com.katyrin.testmapbox.utils.RxBus
import com.katyrin.testmapbox.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        NetworkModule::class,
        PresentationModule::class,
        RepositoryModule::class,
        DataSourceModule::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withContext(context: Context): Builder

        @BindsInstance
        fun withRxBus(rxBus: RxBus): Builder

        fun build(): AppComponent
    }
}