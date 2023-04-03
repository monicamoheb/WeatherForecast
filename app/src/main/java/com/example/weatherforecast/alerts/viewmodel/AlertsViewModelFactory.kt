package com.example.weatherforecast.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.favlocations.viewmodel.FavLocationsViewModel
import com.example.weatherforecast.repo.RepoInterface

class AlertsViewModelFactory (private val _repo: RepoInterface):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertsViewModel::class.java)){
            AlertsViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}