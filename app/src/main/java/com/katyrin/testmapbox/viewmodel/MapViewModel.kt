package com.katyrin.testmapbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.testmapbox.model.repository.MapRepository
import com.mapbox.geojson.Feature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _liveData = MutableLiveData<AppState<List<Feature>>>()
    val liveData: LiveData<AppState<List<Feature>>> = _liveData
    private val disposable = CompositeDisposable()

    private fun updateMarkers(location: Pair<Double, Double>) {
        _liveData.value = AppState.Loading

        disposable += mapRepository.getLocations(location.first, location.second)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::successState, ::setErrorStateServer)
    }

    fun subscribeUpdateMarkers(changeLocation: Flowable<Pair<Double, Double>>) {
        disposable += changeLocation
            .debounce(ONE_SECOND, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::updateMarkers, ::setErrorStateServer)
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

    private companion object {
        const val ONE_SECOND = 1000L
    }
}