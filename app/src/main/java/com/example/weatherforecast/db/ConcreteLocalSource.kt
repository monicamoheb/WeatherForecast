package com.example.weatherforecast.db

import kotlinx.coroutines.flow.Flow
import android.content.Context
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
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

    override suspend fun deleteCurrentWeather() {
       currentWeatherDao.deleteCurrentWeather()
    }

    override suspend fun insertFavLocation(favWeather: FavWeather) {
        currentWeatherDao.insertFavLocation(favWeather)
    }

    override suspend fun getFavLocations(): Flow<List<FavWeather>> {
        return currentWeatherDao.getFavLocations()
    }

    override suspend fun deleteFavLocation(favWeather: FavWeather) {
        currentWeatherDao.deleteFavLocation(favWeather)
    }

    override suspend fun insertAlert(alert: AlertModel) {
        currentWeatherDao.insertAlert(alert)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return  currentWeatherDao.getAllAlerts()
    }

    override suspend fun deleteAlert(alert: AlertModel) {
       currentWeatherDao.deleteAlert(alert)
    }
}