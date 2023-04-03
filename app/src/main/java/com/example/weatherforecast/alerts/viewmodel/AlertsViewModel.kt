package com.example.weatherforecast.alerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "AlertsViewModel"
class AlertsViewModel (private val _repo: RepoInterface) : ViewModel(){
    private var _stateFlow = MutableStateFlow<List<AlertModel>>(listOf())
    val stateFlow: StateFlow<List<AlertModel>> = _stateFlow

    init {
        getAllAlertsDB()
    }

    private fun getAllAlertsDB() = viewModelScope.launch(Dispatchers.IO) {
        _repo.getAllAlerts()
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

    fun deleteAlert(alert: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteAlert(alert)
            getAllAlertsDB()
        }
    }

    fun insertAlert(alert: AlertModel){
        viewModelScope.launch {
            _repo.insertAlert(alert)
        }
    }

}