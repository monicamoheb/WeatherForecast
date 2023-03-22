package com.example.weatherforecast.repo

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    suspend fun getCurrentWeatherOnline(lat:String,lon:String): Response<WeatherResponse>

    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun getCurrentWeatherDB(): Flow<WeatherResponse>
}