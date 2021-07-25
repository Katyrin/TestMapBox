package com.katyrin.testmapbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.ActivityMainBinding
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        savedInstanceState ?: replaceSplashFragment()
    }

    private fun replaceSplashFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SplashFragment.newInstance())
            .commitNow()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}