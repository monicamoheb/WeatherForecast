package com.example.weatherforecast.favlocations.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.FavState
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "FavLocationsViewModel"
class FavLocationsViewModel(private val _repo: RepoInterface) : ViewModel() {
    private var _stateFlow =MutableStateFlow<FavState>(FavState.Loading)
    val stateFlow: StateFlow<FavState> = _stateFlow
    init {
        getFavLocationsDB()
    }

     fun getFavLocationsDB() = viewModelScope.launch {
            _repo.getFavLocations().catch { e->_stateFlow.value=FavState.Failure(e) }
                .collect { data ->
                    if (data != null) {
                        _stateFlow.value = FavState.Success(data)
                    }
                    else{
                        Log.e(TAG, "getFavLocationsDB: data is  null", )
                    }
                }
        }

    fun deleteFavLocation(favWeather: FavWeather) {
        viewModelScope.launch {
            _repo.deleteFavLocation(favWeather)
            getFavLocationsDB()
        }
    }

}