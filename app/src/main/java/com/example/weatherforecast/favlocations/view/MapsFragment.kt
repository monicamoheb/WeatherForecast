package com.example.weatherforecast.favlocations.view

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModel
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModelFactory
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
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
class MapsFragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMapClickListener {

    lateinit var mMap: GoogleMap
    lateinit var geocoder: Geocoder
    lateinit var address: Address
    lateinit var okButton: MaterialButton
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
        geocoder= Geocoder(requireContext())
        okButton=view.findViewById(R.id.ok_btn)
        mapsViewModelFactory= MapsViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )

        mapsViewModel= ViewModelProvider(this, mapsViewModelFactory).get(MapsViewModel::class.java)

        okButton.setOnClickListener{
            var favWeather=FavWeather(address.latitude,address.longitude,address.locality?:address.featureName)
            Log.e(TAG, "onViewCreated: $address", )
            mapsViewModel.insertFavLocation(favWeather)
            Toast.makeText(requireContext(),"Click Okkk ..",Toast.LENGTH_LONG).show()

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap

        mMap.setOnMapClickListener(this)

        var addresses:List<Address> = geocoder.getFromLocationName("london",1) as List<Address>
        if (addresses.size>0) {
             address = addresses.get(0)
            var latLng= LatLng(address.latitude, address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
            Log.i(TAG, "latlng $latLng")
        }

    }

    override fun onMapClick(latLng: LatLng) {
        var addresses:List<Address> = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1) as List<Address>
        if (addresses.size>0) {
            address = addresses.get(0)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
            Log.i(TAG, "latlng $latLng")
        }
    }

}