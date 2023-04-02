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
import com.example.weatherforecast.favlocations.view.MapsFragmentDirections
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
    lateinit var homeViewModelFactory: HomeViewModelFactory
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var layoutManager: LinearLayoutManager
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var binding: FragmentHomeBinding
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

        mySharedPref= MySharedPref(requireContext())
        settings=mySharedPref.sharedPrefRead()

    }

    private fun setUpRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        dailyAdapter = DailyAdapter(ArrayList())
        hourlyAdapter = HourlyAdapter(ArrayList(),requireContext())
        binding.hoursWeatherRecyclerViewHome.adapter = hourlyAdapter
        binding.daysWeatherRecyclerViewHome.adapter = dailyAdapter
    }

    override fun onResume() {
        super.onResume()

        checkNetwork()
    }

    private fun checkNetwork() {
        val networkAvailability = NetworkChecker.isOnline(requireContext())

        Log.e(TAG, "checkNetwork: ${settings.location}", )

        if (networkAvailability) {
            var latlong = arguments?.let { HomeFragmentArgs.fromBundle(it).latlang }
            if(settings.location=="map"&&latlong==null){
                var action: ActionHomeFragmentToMapsFragment=HomeFragmentDirections.actionHomeFragmentToMapsFragment()
                action.sender="home"
                Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_mapsFragment)
            }
            else {
                if (latlong != null) {
                    var latLng = latlong.split("+")
                    viewModel.getCurrentWeatherOnline(
                        latLng.get(0),
                        latLng.get(1),
                        settings.lang,
                        settings.temp
                    )
                } else {
                    if (checkPermissions()) {
                        getCurrentLocation()
                    } else {
                        requestPermissions()
                    }
                }
            }
        }
        else{
            viewModel.getCurrentWeatherDB()
            Snackbar.make(binding.root,
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

                        if(settings.temp=="metric")
                            (binding.tvWeatherDegreeHome.text)=result.data.current.temp.toString()+"°C"
                        else if(settings.temp=="standard")
                            (binding.tvWeatherDegreeHome.text)=result.data.current.temp.toString()+"K"
                        else
                            (binding.tvWeatherDegreeHome.text)=result.data.current.temp.toString()+"F"

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
                        val snackbar = Snackbar.make(binding.root,
                            "Check Your Connection",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
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
            val snackbar = Snackbar.make(binding.root,
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

                viewModel.getCurrentWeatherOnline(lat, long,settings.lang,settings.temp)
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
        var result = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
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
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        setUpRecyclerView()
    }

}