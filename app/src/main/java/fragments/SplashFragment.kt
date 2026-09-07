package com.itanes.appturismo.res.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itanes.appturismo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SplashFragment", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SplashFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SplashFragment", "onViewCreated - Splash VISIBLE")

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            Log.d("SplashFragment", "Navegando a TourList")
            try {
                findNavController().navigate(R.id.action_splash_to_tourList)
                Log.d("SplashFragment", "Navegación exitosa")
            } catch (e: Exception) {
                Log.e("SplashFragment", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}