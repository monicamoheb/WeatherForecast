package com.example.weatherforecast.favlocations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.launch

class MapsViewModel(private val _repo: RepoInterface) : ViewModel() {

    fun insertFavLocation(favWeather: FavWeather){
        viewModelScope.launch {
        _repo.insertFavLocation(favWeather)
        }
    }
}