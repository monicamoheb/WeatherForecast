package com.example.weatherforecast.favlocations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.repo.RepoInterface

class MapsViewModelFactory (private val _repo: RepoInterface):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapsViewModel::class.java)){
            MapsViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}