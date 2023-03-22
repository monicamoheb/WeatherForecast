package com.example.weatherforecast

import com.example.weatherforecast.model.WeatherResponse


sealed class ApiState {
    class Success(val data:WeatherResponse):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()
}