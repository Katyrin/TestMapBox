package com.katyrin.testmapbox.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeoPositionDTO(
    val latitude: Double?,
    val longitude: Double?
) : Parcelable