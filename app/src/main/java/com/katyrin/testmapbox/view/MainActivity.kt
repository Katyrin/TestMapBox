package com.katyrin.testmapbox.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.ActivityMainBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
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

    override fun androidInjector(): AndroidInjector<Any>  = androidInjector
}