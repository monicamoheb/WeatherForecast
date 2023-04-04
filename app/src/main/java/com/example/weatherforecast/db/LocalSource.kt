package com.example.weatherforecast.db

import androidx.room.Delete
import androidx.room.Query
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun getCurrentWeather(): Flow<WeatherResponse>
    suspend fun deleteCurrentWeather()

    suspend fun insertFavLocation(favWeather: FavWeather)
    suspend fun getFavLocations(): Flow<List<FavWeather>>
    suspend fun deleteFavLocation(favWeather: FavWeather)

    suspend fun insertAlert(alert: AlertModel)
    suspend fun getAllAlerts(): Flow<List<AlertModel>>
    suspend fun deleteAlert(alert: AlertModel)
    suspend fun getCurrentWeatherForWorker(): WeatherResponse
}