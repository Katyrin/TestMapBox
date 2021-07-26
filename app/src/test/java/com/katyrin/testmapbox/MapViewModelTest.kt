package com.katyrin.testmapbox

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.katyrin.testmapbox.model.repository.MapRepository
import com.katyrin.testmapbox.viewmodel.AppState
import com.katyrin.testmapbox.viewmodel.MapViewModel
import com.mapbox.geojson.Feature
import io.reactivex.rxjava3.core.Single
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    private var mapViewModel: MapViewModel? = null
    private val mapRepository: MapRepository = mock()
    private val observer: Observer<AppState<List<Feature>>> = mock()
    private val lat = 54.345
    private val lng = 83.784

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val testRule = RxRule()

    @Test
    fun shouldSuccessStateWhenNormalRequest() {
        Mockito.`when`(mapRepository.getLocations(lat, lng)).thenReturn(Single.just(listOf()))
        mapViewModel = MapViewModel(mapRepository)
        mapViewModel?.liveData?.observeForever(observer)
        mapViewModel?.updateMarkers(lat, lng)
        verify(observer).onChanged(Mockito.any(AppState.Success::class.java) as AppState<List<Feature>>?)
    }

    @Test
    fun shouldErrorStateWhenRequestReturnThrowable() {
        Mockito.`when`(mapRepository.getLocations(lat, lng)).thenReturn(Single.error(Throwable()))
        mapViewModel = MapViewModel(mapRepository)
        mapViewModel?.liveData?.observeForever(observer)
        mapViewModel?.updateMarkers(lat, lng)
        verify(observer).onChanged(Mockito.any(AppState.Error::class.java))
    }
}