package com.example.weatherforecast.favlocations.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.NetworkChecker
import com.example.weatherforecast.R
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.favlocations.view.FavoriteFragmentDirections.ActionFavoriteFragmentToMapsFragment
import com.example.weatherforecast.favlocations.viewmodel.FavLocationsViewModel
import com.example.weatherforecast.favlocations.viewmodel.FavLocationsViewModelFactory
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModel
import com.example.weatherforecast.favlocations.viewmodel.MapsViewModelFactory
import com.example.weatherforecast.home.view.HomeFragmentDirections
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FavoriteFragment"
class FavoriteFragment : Fragment() ,OnFavClickListener {

    lateinit var fab:FloatingActionButton
    lateinit var favLocationsViewModel: FavLocationsViewModel
    lateinit var favLocationsViewModelFactory: FavLocationsViewModelFactory
    lateinit var favLocationAdapter: FavLocationAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var favRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onClick(favWeather: FavWeather) {
        favLocationsViewModel.deleteFavLocation(favWeather)
        Toast.makeText(requireContext(),"Removed from favorite list ..",Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab=view.findViewById(R.id.fab)
        favRecyclerView=view.findViewById(R.id.FavRecyclerView)

        fab.setOnClickListener {
            val networkAvailability = NetworkChecker.isOnline(requireContext())
            if (networkAvailability) {
                var action: ActionFavoriteFragmentToMapsFragment=
                    FavoriteFragmentDirections.actionFavoriteFragmentToMapsFragment()
                action.sender="fav"
                Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_mapsFragment)
            }
            else{
                val snackbar = Snackbar.make(view,
                    "Check Your Connection",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
        }

        favLocationsViewModelFactory= FavLocationsViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )

        favLocationsViewModel= ViewModelProvider(this, favLocationsViewModelFactory).get(FavLocationsViewModel::class.java)
        setUpRecyclerView()
        getData()
    }

    fun getData(){
        Log.e(TAG, "getData: ${favLocationsViewModel.stateFlow.value}")
        lifecycleScope.launch(Dispatchers.IO) {
            favLocationsViewModel.stateFlow.collectLatest { favList ->
                favLocationAdapter.vList = favList
                withContext(Dispatchers.Main){
                favLocationAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun setUpRecyclerView(){
        layoutManager= LinearLayoutManager(requireContext())
        layoutManager.orientation= RecyclerView.VERTICAL
        favLocationAdapter= FavLocationAdapter(ArrayList(),this,requireContext())
        favRecyclerView.adapter=favLocationAdapter
        favRecyclerView.layoutManager=layoutManager
    }

}