package com.example.weatherforecast.db

import kotlinx.coroutines.flow.Flow
import android.content.Context
import com.example.weatherforecast.model.WeatherResponse

class ConcreteLocalSource(context: Context) :LocalSource{
    private val currentWeatherDao : CurrentWeatherDao by lazy {
        val db : AppDataBase = AppDataBase.getInstance(context) as AppDataBase
        db.currentWeatherDao()
    }
    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        currentWeatherDao.insertCurrentWeather(weatherResponse)
    }

    override suspend fun getCurrentWeather(): Flow<WeatherResponse> {
        return currentWeatherDao.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather(weatherResponse: WeatherResponse) {
       currentWeatherDao.deleteCurrentWeather(weatherResponse)
    }
}