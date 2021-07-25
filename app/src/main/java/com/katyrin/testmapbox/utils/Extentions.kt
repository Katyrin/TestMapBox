package com.katyrin.testmapbox.utils

import android.Manifest
import android.animation.Animator
import android.app.Activity
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
const val REQUEST_CODE_LOCATION = 54

fun View.setRotateImage(onAnimationEnd: () -> Unit) {
    animate()
        .rotationBy(ROTATION_ANIMATED_AMOUNT)
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd()
            }
        })
        .duration = ROTATION_DURATION
}

fun Activity.requestLocationPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_CODE_LOCATION
    )
}

fun Context.showNoGpsDialog() {
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.dialog_title_no_gps))
        .setMessage(getString(R.string.dialog_message_no_gps))
        .setNegativeButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}

fun Activity.showRationaleDialog() {
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.access_to_location))
        .setMessage(getString(R.string.explanation_get_location))
        .setPositiveButton(getString(R.string.grant_access)) { _, _ -> requestLocationPermission() }
        .setNegativeButton(getString(R.string.do_not)) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}

fun Fragment.checkLocationPermission(onPermissionGranted: () -> Unit) {
    when {
        PermissionsManager.areLocationPermissionsGranted(requireContext()) -> onPermissionGranted()
        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ->
            requireActivity().showRationaleDialog()
        else -> requireActivity().requestLocationPermission()
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}