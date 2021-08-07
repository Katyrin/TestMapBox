package com.katyrin.testmapbox.view

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.katyrin.testmapbox.R
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapStyleBuilder(private val resources: Resources) {

    private fun getMarkerBitmap(): Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)

    private fun getLayer(): Layer =
        SymbolLayer(LAYER_ID, SOURCE_ID)
            .withProperties(
                PropertyFactory.iconImage(ICON_ID),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )

    fun getMapStyleBuilder(listFeature: List<Feature>): Style.Builder =
        Style.Builder()
            .fromUri(Style.MAPBOX_STREETS)
            .withImage(ICON_ID, getMarkerBitmap())
            .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(listFeature)))
            .withLayer(getLayer())

    private companion object {
        private const val ICON_ID = "ICON_ID"
        private const val SOURCE_ID = "SOURCE_ID"
        private const val LAYER_ID = "LAYER_ID"
    }
}