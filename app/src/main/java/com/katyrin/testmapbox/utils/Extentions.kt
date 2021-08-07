package com.katyrin.testmapbox.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.Animator
import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.katyrin.testmapbox.R
import com.mapbox.android.core.permissions.PermissionsManager

private const val ROTATION_ANIMATED_AMOUNT = 1000f
private const val ROTATION_DURATION = 3000L

fun View.setRotateImage(onAnimationEnd: () -> Unit) {
    animate()
        .rotationBy(ROTATION_ANIMATED_AMOUNT)
        .setInterpolator(DecelerateInterpolator())
        .setDuration(ROTATION_DURATION)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd()
            }
        })
}

fun Fragment.requestLocationPermission(): Unit =
    ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(ACCESS_FINE_LOCATION),
        REQUEST_CODE_LOCATION
    )

private fun Fragment.showRationaleDialog(): Unit =
    AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.access_to_location))
        .setMessage(getString(R.string.explanation_get_location))
        .setPositiveButton(getString(R.string.grant_access)) { _, _ -> requestLocationPermission() }
        .setNegativeButton(getString(R.string.do_not)) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()

fun Fragment.checkLocationPermission(onPermissionGranted: () -> Unit): Unit =
    when {
        PermissionsManager.areLocationPermissionsGranted(requireContext()) -> onPermissionGranted()
        shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> showRationaleDialog()
        else -> requestLocationPermission()
    }

fun Context.toast(message: String): Unit =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Fragment.toast(message: String): Unit =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()