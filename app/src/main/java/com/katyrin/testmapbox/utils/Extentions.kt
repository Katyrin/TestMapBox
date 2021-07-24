package com.katyrin.testmapbox.utils

import android.view.View
import android.view.animation.DecelerateInterpolator

private const val ROTATION_ANIMATED_AMOUNT = 1000f
private const val ROTATION_DURATION = 5000L

fun View.setRotateImage() {
    animate()
        .rotationBy(ROTATION_ANIMATED_AMOUNT)
        .setInterpolator(DecelerateInterpolator())
        .duration = ROTATION_DURATION
}