package com.example.weatherforecast.db

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun getCurrentWeather(): Flow<WeatherResponse>
    suspend fun deleteCurrentWeather(weatherResponse: WeatherResponse)
}