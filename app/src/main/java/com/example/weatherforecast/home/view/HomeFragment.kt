package com.example.weatherforecast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.MySharedPref
import com.example.weatherforecast.NetworkChecker
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.home.view.HomeFragmentDirections.ActionHomeFragmentToMapsFragment
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.model.SettingsModel
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_ID = 40

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    lateinit var viewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var binding: FragmentHomeBinding
    lateinit var mySharedPref: MySharedPref
    lateinit var settings: SettingsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        mySharedPref = MySharedPref(requireContext())
        settings = mySharedPref.sharedPrefRead()

    }

    private fun setUpRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        dailyAdapter = DailyAdapter(ArrayList())
        hourlyAdapter = HourlyAdapter(ArrayList(), requireContext())
        binding.hoursWeatherRecyclerViewHome.adapter = hourlyAdapter
        binding.daysWeatherRecyclerViewHome.adapter = dailyAdapter
    }

    override fun onResume() {
        super.onResume()

        checkNetwork()
        binding.swipeRefresh.setOnRefreshListener {
            checkNetwork()
            binding.swipeRefresh.isRefreshing = false
        }

    }

    private fun checkNetwork() {
        val networkAvailability = NetworkChecker.isOnline(requireContext())

        Log.e(TAG, "checkNetwork: ${settings.location}")

        if (networkAvailability) {
            val latLong = arguments?.let { HomeFragmentArgs.fromBundle(it).latlang }
            if (settings.location == "map" && latLong == null) {
                val action: ActionHomeFragmentToMapsFragment =
                    HomeFragmentDirections.actionHomeFragmentToMapsFragment()
                action.sender = "home"
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_homeFragment_to_mapsFragment)
            } else {
                if (latLong != null) {
                    val latLng = latLong.split("+")
                    viewModel.getCurrentWeatherOnline(
                        latLng[0],
                        latLng[1],
                        settings.lang,
                        settings.temp
                    )
                    requireArguments().clear()
                } else {
                    if (checkPermissions()) {
                        getCurrentLocation()
                    } else {
                        requestPermissions()
                    }
                }
            }
        } else {
            viewModel.getCurrentWeatherDB()
            Snackbar.make(
                binding.root,
                "No Internet ..",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        getData()
    }

    private fun getData() {
        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        Log.i(TAG, "getData: loading")
                        binding.homeProgressbar.visibility = View.VISIBLE
                        binding.cardViewHome.visibility = View.GONE
                        binding.detailsCardViewHome.visibility = View.GONE
                        binding.tvCity.visibility = View.GONE
                        binding.tvDate.visibility = View.GONE

                    }
                    is ApiState.Success -> {
                        binding.homeProgressbar.visibility = View.GONE
                        binding.cardViewHome.visibility = View.VISIBLE
                        binding.detailsCardViewHome.visibility = View.VISIBLE
                        binding.tvCity.visibility = View.VISIBLE
                        binding.tvDate.visibility = View.VISIBLE

                        dailyAdapter.dList = result.data.daily
                        hourlyAdapter.hList = result.data.hourly
                        dailyAdapter.notifyDataSetChanged()
                        hourlyAdapter.notifyDataSetChanged()

                        binding.tvCity.text = result.data.timezone
                        binding.tvDate.text = getDateTime()
                        binding.tvWeatherStatusHome.text =
                            result.data.current.weather.get(0).description

                        if (settings.temp == "metric") {
                            if (settings.windSpeed == "meter/sec") {
                                binding.tvWind.text =
                                    result.data.current.wind_speed.toString() + " m/s"
                            } else {
                                binding.tvWind.text = (result.data.current.wind_speed).times(2.237)
                                    .toString() + " m/h"

                            }
                            (binding.tvWeatherDegreeHome.text) =
                                result.data.current.temp.toString() + "°C"
                        } else if (settings.temp == "standard") {
                            if (settings.windSpeed == "meter/sec") {
                                binding.tvWind.text =
                                    result.data.current.wind_speed.toString() + " m/s"
                            } else {
                                binding.tvWind.text = (result.data.current.wind_speed).times(2.237)
                                    .toString() + " m/h"
                            }
                            (binding.tvWeatherDegreeHome.text) =
                                result.data.current.temp.toString() + "K"
                        } else {
                            if (settings.windSpeed == "meter/sec") {
                                binding.tvWind.text =
                                    (result.data.current.wind_speed).div(2.237).toString() + " m/h"
                            } else {
                                binding.tvWind.text =
                                    result.data.current.wind_speed.toString() + " m/s"
                            }
                            (binding.tvWeatherDegreeHome.text) =
                                result.data.current.temp.toString() + "F"
                        }

                        Glide.with(binding.IVWeatherStatusIconHome.context)
                            .load(
                                "https://openweathermap.org/img/wn/${
                                    result.data.current.weather[0].icon
                                }@2x.png"
                            )
                            .into(binding.IVWeatherStatusIconHome)
                        Log.i(TAG, "onCreate: " + result.data)

                        binding.tvHumidity.text = result.data.current.humidity.toString() + "%"
                        binding.tvPressure.text = result.data.current.pressure.toString() + " hpa"
                        binding.tvCloud.text = result.data.current.clouds.toString() + "%"
                        binding.tvUltraViolet.text = result.data.current.uvi.toString()
                        binding.tvVisibility.text = result.data.current.visibility.toString() + " m"

                        Log.e(TAG, "getData: " + result.data.current.visibility.toString())
                    }
                    else -> {
                        //image failure
                        binding.homeProgressbar.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            "Check Your Connection",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun getDateTime(): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy ",Locale.getDefault())
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
            val snackbar = Snackbar.make(
                binding.root,
                "Turn on location",
                Snackbar.LENGTH_SHORT
            )
            snackbar.show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    @Deprecated("Deprecated in Java")
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
        locationRequest.numUpdates = 3
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

                val lat = lastLocation.latitude.toString()
                val long = lastLocation.longitude.toString()

                settings = mySharedPref.sharedPrefRead()
                viewModel.getCurrentWeatherOnline(lat, long, settings.lang, settings.temp)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        setUpRecyclerView()
    }

}