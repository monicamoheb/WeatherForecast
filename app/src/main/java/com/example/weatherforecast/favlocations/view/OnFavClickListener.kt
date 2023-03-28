package com.example.weatherforecast.favlocations.view

import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.WeatherResponse

interface OnFavClickListener {
    fun onClick(weather: FavWeather)
}