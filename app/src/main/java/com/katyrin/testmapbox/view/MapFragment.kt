package com.katyrin.testmapbox.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.utils.RxBus
import com.katyrin.testmapbox.databinding.FragmentMapBinding
import com.katyrin.testmapbox.utils.checkLocationPermission
import com.katyrin.testmapbox.utils.toast
import com.katyrin.testmapbox.viewmodel.AppState
import com.katyrin.testmapbox.viewmodel.MapViewModel
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.Style.MAPBOX_STREETS
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject


class MapFragment : Fragment(), OnMapReadyCallback, HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var rxBus: RxBus
    private val disposable = CompositeDisposable()

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MapViewModel by viewModels(factoryProducer = { factory })
    private var binding: FragmentMapBinding? = null
    private var mapboxMap: MapboxMap? = null
    private val mapStyleBuilder: MapStyleBuilder by lazy { MapStyleBuilder(resources) }
    private val _changeLocation = BehaviorSubject.create<Pair<Double, Double>>()
    private val changeLocation = _changeLocation.toFlowable(BackpressureStrategy.LATEST)

    private val locationListener = object : LocationListener {
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onLocationChanged(location: Location) {
            _changeLocation.onNext(location.latitude to location.longitude)
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Mapbox.getInstance(requireContext(), getString(R.string.access_token))
        return FragmentMapBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.getMapAsync(this)
        viewModel.liveData.observe(viewLifecycleOwner) { renderData(it) }
        viewModel.subscribeUpdateMarkers(changeLocation)
        disposable += rxBus.subscribe { getLocation() }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeUpdateLocation() {
        (context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager).apply {
            getProvider(LocationManager.GPS_PROVIDER)?.let {
                requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    REFRESH_PERIOD,
                    MINIMAL_DISTANCE,
                    locationListener
                )
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        getLocation()
    }

    private fun updateMarkers(): Unit? =
        mapboxMap?.locationComponent?.lastKnownLocation?.let { location ->
            _changeLocation.onNext(location.latitude to location.longitude)
        }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val locationComponentOptions = LocationComponentOptions.builder(requireContext()).build()
        mapboxMap?.locationComponent?.apply {
            activateLocationComponent(
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build()
            )
            isLocationComponentEnabled = true
            cameraMode = CameraMode.TRACKING
            renderMode = RenderMode.NORMAL
        }
    }

    override fun onStart() {
        super.onStart()
        binding?.mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding?.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding?.mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding?.mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        mapboxMap = null
        binding = null
        disposable.dispose()
        super.onDestroyView()
        binding?.mapView?.onDestroy()
    }

    private fun getLocation(): Unit? =
        mapboxMap?.setStyle(MAPBOX_STREETS) { style ->
            checkLocationPermission {
                enableLocationComponent(style)
                updateMarkers()
                subscribeUpdateLocation()
            }
        }

    private fun renderData(appState: AppState<List<Feature>>) {
        when (appState) {
            is AppState.Success -> showMarkers(appState.value)
            is AppState.Error -> toast(getString(R.string.server_error) + " ${appState.message}")
            is AppState.Loading -> toast(getString(R.string.loading))
        }
    }

    private fun showMarkers(listFeature: List<Feature>): Unit? =
        mapboxMap?.setStyle(mapStyleBuilder.getMapStyleBuilder(listFeature))

    companion object {
        private const val REFRESH_PERIOD = 10000L
        private const val MINIMAL_DISTANCE = 50f
        fun newInstance() = MapFragment()
    }
}