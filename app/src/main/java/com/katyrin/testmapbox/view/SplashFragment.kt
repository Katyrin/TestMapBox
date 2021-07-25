package com.katyrin.testmapbox.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katyrin.testmapbox.R
import com.katyrin.testmapbox.databinding.FragmentSplashBinding
import com.katyrin.testmapbox.utils.setRotateImage

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
    }

    private fun replaceMapFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment.newInstance())
            .commit()
    }

    override fun onDetach() {
        binding = null
        super.onDetach()
    }

    companion object {
        fun newInstance() = SplashFragment()
    }
}