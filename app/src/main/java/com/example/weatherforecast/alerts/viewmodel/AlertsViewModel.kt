package com.example.weatherforecast.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.AlertState
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "AlertsViewModel"
class AlertsViewModel (private val _repo: RepoInterface) : ViewModel(){
    private var _stateFlow = MutableStateFlow<AlertState>(AlertState.Loading)
    val stateFlow: StateFlow<AlertState> = _stateFlow

    init {
        getAllAlertsDB()
    }

    private fun getAllAlertsDB() = viewModelScope.launch {
        _repo.getAllAlerts().catch { e-> _stateFlow.value=AlertState.Failure(e) }
            .collect { data ->
                if (data != null) {
                    _stateFlow.value = AlertState.Success(data)
//                    Log.e(TAG, "getFavLocationsDB: $data" )
                }
                else{
//                    Log.e(TAG, "getFavLocationsDB: data is  null", )
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