package com.katyrin.testmapbox.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.FragmentSplashBinding
import com.katyrin.testmapbox.utils.setRotateImage
import com.katyrin.testmapbox.viewmodel.SplashViewModel

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private var binding: FragmentSplashBinding? = null
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSplashBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        startAnimation()
    }

    private fun startAnimation() {
        binding?.imageView?.setRotateImage()
        handler.postDelayed({ checkPermission() }, SPLASH_ACTIVITY_ANIMATION_TIME)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION
        )
    }

    private fun checkPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> replaceMapFragment()
            else -> requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    replaceMapFragment()
                else showRequestDialog()
                return
            }
        }
    }

    private fun showRequestDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.access_to_location))
            .setMessage(getString(R.string.explanation_get_location))
            .setPositiveButton(getString(R.string.grant_access)) { _, _ ->
                requestPermissions()
                replaceMapFragment()
            }
            .setNegativeButton(getString(R.string.do_not)) { dialog, _ ->
                dialog.dismiss()
                replaceMapFragment()
            }
            .create()
            .show()
    }

    private fun replaceMapFragment() {

    }

    override fun onDetach() {
        binding = null
        handler.removeCallbacksAndMessages(null)
        super.onDetach()
    }

    companion object {
        private const val SPLASH_ACTIVITY_ANIMATION_TIME = 1000L
        private const val REQUEST_CODE_LOCATION = 54
        fun newInstance() = SplashFragment()
    }
}