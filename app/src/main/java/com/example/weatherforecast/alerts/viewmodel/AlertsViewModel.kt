package com.example.weatherforecast.alerts.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.AlertApiState
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "AlertsViewModel"
class AlertsViewModel (private val _repo: RepoInterface) : ViewModel(){
    private var _stateFlow = MutableStateFlow<AlertApiState>(AlertApiState.Loading)
    val stateFlow: StateFlow<AlertApiState> = _stateFlow

    init {
        getAllAlertsDB()
    }

    private fun getAllAlertsDB() = viewModelScope.launch {
        _repo.getAllAlerts().catch { e-> _stateFlow.value=AlertApiState.Failure(e) }
            .collect { data ->
                if (data != null) {
                    _stateFlow.value = AlertApiState.Success(data)
                    Log.e(TAG, "getFavLocationsDB: $data" )
                }
                else{
                    Log.e(TAG, "getFavLocationsDB: data is  null", )
                }
            }
    }

    fun deleteAlert(alert: AlertModel) {
        viewModelScope.launch {
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