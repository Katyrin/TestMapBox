package com.katyrin.testmapbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.testmapbox.model.repository.MapRepository
import com.mapbox.geojson.Feature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _liveData = MutableLiveData<AppState<List<Feature>>>()
    val liveData: LiveData<AppState<List<Feature>>> = _liveData
    private val disposable = CompositeDisposable()

    fun updateMarkers(latitude: Double, longitude: Double) {
        _liveData.value = AppState.Loading

        disposable += mapRepository.getLocations(latitude, longitude)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::successState, ::setErrorStateServer)
    }

    private fun successState(features: List<Feature>) {
        _liveData.value = AppState.Success(features)
    }

    private fun setErrorStateServer(throwable: Throwable) {
        _liveData.value = AppState.Error(throwable.message)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}