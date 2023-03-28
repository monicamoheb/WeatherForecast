package com.example.weatherforecast.favlocations.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "FavLocationsViewModel"
class FavLocationsViewModel(private val _repo: RepoInterface) : ViewModel() {
    private var _stateFlow = MutableStateFlow<List<FavWeather>>(listOf())
    val stateFlow: StateFlow<List<FavWeather>> = _stateFlow

    init {
        getFavLocationsDB()
    }

    fun getFavLocationsDB() = viewModelScope.launch(Dispatchers.IO) {
            _repo.getFavLocations()
                .collect { data ->
                    if (data != null) {
                        _stateFlow.value = data
                        Log.e(TAG, "getFavLocationsDB: $data" )
                    }
                    else{
                        Log.e(TAG, "getFavLocationsDB: data is  null", )
                    }
                }
        }

    fun deleteFavLocation(favWeather: FavWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteFavLocation(favWeather)
            getFavLocationsDB()
        }
    }

}