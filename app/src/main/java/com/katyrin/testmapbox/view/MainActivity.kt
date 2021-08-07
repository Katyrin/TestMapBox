package com.katyrin.testmapbox.view

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.utils.RxBus
import com.katyrin.testmapbox.databinding.ActivityMainBinding
import com.katyrin.testmapbox.utils.REQUEST_CODE_LOCATION
import com.katyrin.testmapbox.utils.toast
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var rxBus: RxBus
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
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> checkPermissionsResult(grantResults)
            else -> toast(getString(R.string.permission_denied))
        }
    }

    private fun checkPermissionsResult(grants: IntArray) {
        if (grants.isNotEmpty() && grants[0] == PackageManager.PERMISSION_GRANTED) rxBus.publish()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}