package com.example.weatherforecast.repo

import androidx.room.Delete
import androidx.room.Query
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    suspend fun getCurrentWeatherOnline(lat:String,lon:String): Flow<WeatherResponse>

    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()
    suspend fun getCurrentWeatherDB(): Flow<WeatherResponse>

    suspend fun insertFavLocation(favWeather: FavWeather)
    suspend fun getFavLocations(): Flow<List<FavWeather>>
    suspend fun deleteFavLocation(favWeather: FavWeather)
}