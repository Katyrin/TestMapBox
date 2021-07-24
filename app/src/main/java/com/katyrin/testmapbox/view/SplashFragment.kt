package com.katyrin.testmapbox.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.FragmentSplashBinding
import com.katyrin.testmapbox.utils.*

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSplashBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation()
    }

    private fun startAnimation() {
        binding?.imageView?.setRotateImage { replaceMapFragment() }
        checkPermission()
    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
        when {
            permission == PackageManager.PERMISSION_GRANTED -> getLocation()
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ->
                requireActivity().showRationaleDialog()
            else -> requireActivity().requestLocationPermission()
        }
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
        if (permission == PackageManager.PERMISSION_GRANTED) replaceMapFragment()
        else requireActivity().showRationaleDialog()
    }

    private fun replaceMapFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment.newInstance())
            .commitNow()
    }

    override fun onDetach() {
        binding = null
        super.onDetach()
    }

    companion object {
        fun newInstance() = SplashFragment()
    }
}