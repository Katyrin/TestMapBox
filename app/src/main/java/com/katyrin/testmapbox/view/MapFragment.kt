package com.katyrin.testmapbox.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.katyrin.testmapbox.BuildConfig
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.FragmentMapBinding
import com.katyrin.testmapbox.utils.*
import com.katyrin.testmapbox.viewmodel.AppState
import com.katyrin.testmapbox.viewmodel.MapViewModel
import com.mapbox.android.core.permissions.PermissionsListener
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
import javax.inject.Inject


class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener, HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MapViewModel by viewModels(factoryProducer = { factory })
    private var binding: FragmentMapBinding? = null
    private var mapboxMap: MapboxMap? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentMapBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.getMapAsync(this)
        viewModel.liveData.observe(viewLifecycleOwner) { renderData(it) }
        checkLocationPermission(::updateLocation)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        context?.let { ctx ->
            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.getProvider(LocationManager.GPS_PROVIDER)?.let {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    REFRESH_PERIOD,
                    MINIMAL_DISTANCE
                ) { location -> viewModel.updateMarkers(location.latitude, location.longitude) }
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(MAPBOX_STREETS) { style ->
            checkLocationPermission { enableLocationComponent(style) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val locationComponentOptions = LocationComponentOptions.builder(requireContext()).build()
        val locationComponent = mapboxMap?.locationComponent

        locationComponent?.activateLocationComponent(
            LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(locationComponentOptions)
                .build()
        )
        locationComponent?.isLocationComponentEnabled = true
        locationComponent?.cameraMode = CameraMode.TRACKING
        locationComponent?.renderMode = RenderMode.NORMAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(requireContext(), BuildConfig.MAP_ACCESS_TOKEN)
        super.onCreate(savedInstanceState)
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
        super.onDestroyView()
        binding?.mapView?.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            checkPermissionsResult(grantResults)
            return
        }
    }

    private fun checkPermissionsResult(grants: IntArray) {
        if (grants.isNotEmpty() && grants.size == countPermissions(grants)) getLocation()
        else requireContext().showNoGpsDialog()
    }

    private fun countPermissions(grantResults: IntArray): Int {
        var grantedPermissions = 0
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) grantedPermissions++
        }
        return grantedPermissions
    }

    private fun getLocation() {
        val permission = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) requireActivity().showRationaleDialog()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        requireContext().toast(getString(R.string.explanation_get_location))
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted)
            mapboxMap?.getStyle { style -> checkLocationPermission { enableLocationComponent(style) } }
        else
            requireContext().toast(getString(R.string.not_granted_message))
    }

    private fun renderData(appState: AppState<List<Feature>>) {
        when (appState) {
            is AppState.Success -> showMarkers(appState.value)
            is AppState.Error ->
                requireContext().toast(getString(R.string.server_error) + " ${appState.message}")
            is AppState.Loading -> requireContext().toast(getString(R.string.loading))
        }
    }

    private fun showMarkers(listFeature: List<Feature>) {
        mapboxMap?.setStyle(
            MapStyleBuilder(listFeature, resources).getMapStyleBuilder()
        ) { style -> checkLocationPermission { enableLocationComponent(style) } }
    }

    companion object {
        private const val REFRESH_PERIOD = 60000L
        private const val MINIMAL_DISTANCE = 100f
        fun newInstance() = MapFragment()
    }
}