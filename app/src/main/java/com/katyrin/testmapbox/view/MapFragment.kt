package com.katyrin.testmapbox.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.FragmentMapBinding
import com.katyrin.testmapbox.utils.*
import com.katyrin.testmapbox.viewmodel.MapViewModel
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.Style.MAPBOX_STREETS
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private lateinit var viewModel: MapViewModel
    private var binding: FragmentMapBinding? = null
    private var mapboxMap: MapboxMap? = null

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
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        checkLocationPermission { updateLocation() }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        context?.let { ctx ->
            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.getProvider(LocationManager.GPS_PROVIDER)?.let {
                if (PermissionsManager.areLocationPermissionsGranted(ctx)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        REFRESH_PERIOD,
                        MINIMAL_DISTANCE
                    ) { location ->
                        viewModel.updateMarkers(location.latitude, location.longitude)
                        Toast.makeText(ctx, "updateMarkers", Toast.LENGTH_LONG).show()
                    }
                }
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
        Mapbox.getInstance(
            requireContext(),
            "sk.eyJ1Ijoia2F0eXJpbiIsImEiOiJja3JodHdxNTkwMGM0Mm9wNjlteHVtcno1In0.BMY9DSah4Xj4RBX9_19otw"
        )
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
        val permission = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission != PackageManager.PERMISSION_GRANTED) requireActivity().showRationaleDialog()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(requireContext(), R.string.explanation_get_location, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted)
            mapboxMap?.getStyle { style -> checkLocationPermission { enableLocationComponent(style) } }
        else
            Toast.makeText(requireContext(), R.string.not_granted_message, Toast.LENGTH_LONG).show()
    }

    private fun updateMarkers(listFeature: List<Feature>) {
        Style.Builder()
            .withSource(GeoJsonSource("SOURCE_ID", FeatureCollection.fromFeatures(listFeature)))
    }

    companion object {
        private const val REFRESH_PERIOD = 60000L
        private const val MINIMAL_DISTANCE = 100f
        fun newInstance() = MapFragment()
    }
}