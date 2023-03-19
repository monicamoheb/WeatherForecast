package com.example.weatherforecast.repo

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    suspend fun getLocation(lat:String,lon:String): Response<WeatherResponse>

    suspend fun insertFavLocation(weatherResponse: WeatherResponse)
    suspend fun deleteFavLocation(weatherResponse: WeatherResponse)
    suspend fun getAllFavLocation(): Flow<List<WeatherResponse>>
}