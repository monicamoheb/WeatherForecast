package com.example.weatherforecast

import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse

sealed class ApiState {
    class Success(val data:WeatherResponse):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()
}

sealed class FavState {
    class Success(val favData:List<FavWeather>):FavState()
    class Failure(val msg:Throwable):FavState()
    object Loading:FavState()
}

sealed class AlertState {
    class Success(val alertData:List<AlertModel>):AlertState()
    class Failure(val msg:Throwable):AlertState()
    object Loading:AlertState()
}