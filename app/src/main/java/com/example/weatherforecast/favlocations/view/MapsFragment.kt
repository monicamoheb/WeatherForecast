package com.example.weatherforecast.favlocations.view

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.weatherforecast.R
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.db.CurrentWeatherDao
import com.example.weatherforecast.favlocations.view.MapsFragmentDirections.ActionMapsFragmentToHomeFragment
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModel
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModelFactory
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton

private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    lateinit var mMap: GoogleMap
    lateinit var geocoder: Geocoder
    lateinit var address: Address
    lateinit var okButton: MaterialButton
    lateinit var searchEditText: EditText
    lateinit var backButton: ImageButton
    lateinit var mapsViewModel: MapsViewModel
    lateinit var mapsViewModelFactory: MapsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        geocoder = Geocoder(requireContext())
        okButton = view.findViewById(R.id.ok_btn)
        searchEditText=view.findViewById(R.id.search_editText)
        backButton=view.findViewById(R.id.back_img_btn)

        val currentWeatherDao : CurrentWeatherDao by lazy {
            val db : AppDataBase = AppDataBase.getInstance(requireContext()) as AppDataBase
            db.currentWeatherDao()
        }
        mapsViewModelFactory = MapsViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(currentWeatherDao)
            )
        )

        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory).get(MapsViewModel::class.java)
        var sender = arguments?.let { MapsFragmentArgs.fromBundle(it).sender }
        backButton.setOnClickListener {
            if (sender != null) {
                if(sender=="fav") {
                    Navigation.findNavController(view).navigate(R.id.favoriteFragment)
                }
                else if (sender=="home"){
                    Navigation.findNavController(view).navigate(R.id.homeFragment)
                }
            }
        }
        okButton.setOnClickListener {
            Log.e(TAG, "onViewCreated: sender: $sender", )
            if (sender != null) {
                if(sender=="fav"){
                    Log.e(TAG, "onViewCreated: addresssss:  $address", )
                    var favWeather = FavWeather(
                        address.latitude,
                        address.longitude,
                        address.getAddressLine(0)
                    )
                    mapsViewModel.insertFavLocation(favWeather)
                    Toast.makeText(requireContext(), "Click Okkk ..", Toast.LENGTH_LONG).show()
                    Navigation.findNavController(view).navigate(R.id.favoriteFragment)

                } else if (sender=="home") {

                    var action: ActionMapsFragmentToHomeFragment =
                        MapsFragmentDirections.actionMapsFragmentToHomeFragment()
                    action.latlang = "${address.latitude}+${address.longitude}"
                    Navigation.findNavController(view).navigate(action)
                    action.latlang = null
                }
            }

        }
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            val address = geocoder.getFromLocationName(searchEditText.text.toString(), 1)
            if (address != null) {
                val searchedLatLng = LatLng(address[0].latitude, address[0].longitude)
                this.address.latitude=searchedLatLng.latitude
                this.address.longitude=searchedLatLng.longitude
                this.address.setAddressLine(0,address[0].getAddressLine(0))
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(searchedLatLng).title(address[0].getAddressLine(0)))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLng, 16F))
            }
            return@setOnEditorActionListener false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        var addresses: List<Address> = geocoder.getFromLocationName("london", 1) as List<Address>
        if (addresses.size > 0) {
            address = addresses.get(0)
            var latLng = LatLng(address.latitude, address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
            Log.i(TAG, "latlng $latLng")
        }

    }

    override fun onMapClick(latLng: LatLng) {
        var addresses: List<Address> =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as List<Address>
        if (addresses.size > 0) {
            address = addresses.get(0)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
            Log.i(TAG, "latlng $latLng")
        }
    }

}