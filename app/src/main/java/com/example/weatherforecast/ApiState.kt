package com.example.weatherforecast

import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse


sealed class ApiState {
    class Success(val data:WeatherResponse):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()
}

sealed class FavApiState {
    class Success(val favData:List<FavWeather>):FavApiState()
    class Failure(val msg:Throwable):FavApiState()
    object Loading:FavApiState()
}

sealed class AlertApiState {
    class Success(val alertData:List<AlertModel>):AlertApiState()
    class Failure(val msg:Throwable):AlertApiState()
    object Loading:AlertApiState()
}