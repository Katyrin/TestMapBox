package com.katyrin.testmapbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katyrin.testmapbox.model.data.GeoPositionDTO

class MapViewModel : ViewModel() {

    private val _liveData = MutableLiveData<AppState<List<GeoPositionDTO>>>()
    val liveData: LiveData<AppState<List<GeoPositionDTO>>> = _liveData

    fun updateMarkers(latitude: Double, longitude: Double) {
        _liveData.value = AppState.Loading
    }
}