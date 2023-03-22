package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSource {
    //lang unit
    suspend fun getLocationOnline(lat:String,lon:String): Flow<WeatherResponse>
}