package com.example.weatherforecast.repo

import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getCurrentWeatherOnline(lat:String,lon:String,lang:String,unit: String): Flow<WeatherResponse>

    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteCurrentWeather()
    suspend fun getCurrentWeatherDB(): Flow<WeatherResponse>

    suspend fun insertFavLocation(favWeather: FavWeather)
    suspend fun getFavLocations(): Flow<List<FavWeather>>
    suspend fun deleteFavLocation(favWeather: FavWeather)
}