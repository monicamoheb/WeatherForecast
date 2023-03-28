package com.example.weatherforecast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.NetworkChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val PERMISSION_ID = 40

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    lateinit var viewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var layoutManager: LinearLayoutManager
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

    }

    private fun setUpRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        dailyAdapter = DailyAdapter(ArrayList())
        hourlyAdapter = HourlyAdapter(ArrayList())
        binding.hoursWeatherRecyclerViewHome.adapter = hourlyAdapter
        binding.daysWeatherRecyclerViewHome.adapter = dailyAdapter

    }

    override fun onResume() {
        super.onResume()
        checkNetwork()
    }

    fun checkNetwork() {
        val networkAvailability = NetworkChecker.isOnline(requireContext())
        if (networkAvailability) {
            if (checkPermissions()) {
                getCurrentLocation()
            } else {
                requestPermissions()
            }
        }
        else{
            viewModel.getCurrentWeatherDB()
        }

        getData()
    }

    fun getData() {

        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        Log.i(TAG, "getData: loading")
                        binding.homeProgressbar.visibility=View.VISIBLE
                        binding.cardViewHome.visibility=View.GONE
                        binding.detailsCardViewHome.visibility=View.GONE
                        binding.tvCity.visibility=View.GONE
                        binding.tvDate.visibility=View.GONE

                    }
                    is ApiState.Success -> {
                        binding.homeProgressbar.visibility=View.GONE
                        binding.cardViewHome.visibility=View.VISIBLE
                        binding.detailsCardViewHome.visibility=View.VISIBLE
                        binding.tvCity.visibility=View.VISIBLE
                        binding.tvDate.visibility=View.VISIBLE

                        dailyAdapter.dList = result.data.daily
                        hourlyAdapter.hList = result.data.hourly
                        dailyAdapter.notifyDataSetChanged()
                        hourlyAdapter.notifyDataSetChanged()

                        binding.tvCity.text=result.data.timezone
                        binding.tvDate.text=getDateTime()
                        binding.tvWeatherStatusHome.text=result.data.current.weather.get(0).description
                        var cel=result.data.current.temp - 273.15
                        binding.tvWeatherDegreeHome.text= cel.toFloat().toString()+" °C"
                        Glide.with(binding.IVWeatherStatusIconHome.context)
                            .load("https://openweathermap.org/img/wn/${result.data.current.weather.get(0).icon}@2x.png")
                            .into(binding.IVWeatherStatusIconHome)
                        Log.i(TAG, "onCreate: " + result.data)

                        binding.tvHumidity.text=result.data.current.humidity.toString()+"%"
                        binding.tvPressure.text= result.data.current.pressure.toString()+" hpa"
                        binding.tvCloud.text= result.data.current.clouds.toString()+"%"
                        binding.tvWind.text= result.data.current.wind_speed.toString()+" m/s"
                        binding.tvUltraViolet.text= result.data.current.uvi.toString()
                        binding.tvVisibility.text= result.data.current.visibility.toString()+" m"

                        Log.e(TAG, "getData: "+result.data.current.visibility.toString() )
                    }
                    else-> {
                        //image failure
                        binding.homeProgressbar.visibility=View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Check Your Connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    private fun getDateTime(): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy - h:m:s")
            sdf.format(Date())
        } catch (e: Exception) {
            e.toString()
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {

        if (isLocationEnabled()) {
            requestNewLocationData()
        } else {
            Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(1)
        locationRequest.numUpdates=3
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            if (lastLocation != null) {

                var lat = lastLocation.latitude.toString()
                var long = lastLocation.longitude.toString()

                viewModel.getCurrentWeatherOnline(lat, long)

            }
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun checkPermissions(): Boolean {
        var result = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        Log.i("hossam", "checkPermissions: $result")
        Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_LONG).show()
        return result
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var remoteSource = LocationClient.getInstance()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        lifecycleScope.launch(Dispatchers.IO) {
            Log.i(
                TAG,
                "onViewCreated: " + remoteSource.getLocationOnline("33.44", "94.04").toString()
            )
        }
        setUpRecyclerView()
    }

}