package com.example.weatherforecast.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.R
import com.example.weatherforecast.network.LocationClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var remoteSource=LocationClient.getInstance()
        lifecycleScope.launch(Dispatchers.IO) {
            Log.e(TAG, "onViewCreated: "+ remoteSource.getLocationOnline("33.44","94.04").body()?.current?.humidity)

        }
    }

}