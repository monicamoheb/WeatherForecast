package com.example.weatherforecast.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.ApiState
import com.example.weatherforecast.NetworkChecker
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: RepoInterface) : ViewModel() {

    private var _stateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val stateFlow: StateFlow<ApiState> = _stateFlow

    fun getCurrentWeatherOnline(lat: String, lon: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getCurrentWeatherOnline(lat, lon)
                .collect { data ->
                    _repo.deleteCurrentWeather()
                    _repo.insertCurrentWeather(data)
                    getCurrentWeatherDB()
                }
        }

     fun getCurrentWeatherDB() =
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getCurrentWeatherDB().catch { _stateFlow.value = ApiState.Failure(it) }
                .collect { data ->
                    if (data != null) {
                        _stateFlow.value = ApiState.Success(data)
                    } else {
                        //_stateFlow.value = ApiState.Failure(Throwable("empty data in database"))
                    }
                }
        }

}

