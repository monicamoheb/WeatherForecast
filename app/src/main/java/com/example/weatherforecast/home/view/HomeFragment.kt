package com.example.weatherforecast.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.NetworkChecker
import com.example.weatherforecast.R
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    lateinit var viewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModelFactory= HomeViewModelFactory(
            Repo.getInstance(LocationClient.getInstance(),
            ConcreteLocalSource(requireContext())))
        viewModel=ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)

        val networkAvailability= NetworkChecker.isOnline(requireContext())

        if (networkAvailability){

        }

        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest { result->
                when(result){
                    is ApiState.Loading->{

                    }
                    is ApiState.Success->{

//                        favAdapter.vList=result.data
//                        favAdapter.notifyDataSetChanged()
                        Log.e(TAG, "onCreate: "+ result.data )
                    }
                    else->{
                        Toast.makeText(requireContext(),
                            "Check Your Connection",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
            Log.e(TAG, "onViewCreated: "+ remoteSource.getLocationOnline("33.44","94.04").toString())
        }
    }

}