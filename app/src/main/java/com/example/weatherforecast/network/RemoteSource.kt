package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    //lang unit
    suspend fun getLocationOnline(lat:String,lon:String): Response<WeatherResponse>
}