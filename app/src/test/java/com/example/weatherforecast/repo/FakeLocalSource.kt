package com.example.weatherforecast.repo

import com.example.weatherforecast.db.LocalSource
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    var weather: WeatherResponse,
    var favWeatherList: MutableList<FavWeather>? = mutableListOf(),
    var alertsList: MutableList<AlertModel>? = mutableListOf()
) : LocalSource {
    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        weather = weatherResponse
    }

    override suspend fun getCurrentWeather(): Flow<WeatherResponse> {
        return flowOf(weather)
    }

    override suspend fun deleteCurrentWeather() {

    }

    override suspend fun insertFavLocation(favWeather: FavWeather) {
        favWeatherList?.add(favWeather)
    }

    override suspend fun getFavLocations(): Flow<List<FavWeather>> {
        return flowOf(favWeatherList!!)
    }

    override suspend fun deleteFavLocation(favWeather: FavWeather) {
        favWeatherList?.remove(favWeather)
    }

    override suspend fun insertAlert(alert: AlertModel) {
       alertsList?.add(alert)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return flowOf(alertsList!!)
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        alertsList?.remove(alert)
    }

    override suspend fun getCurrentWeatherForWorker(): WeatherResponse {
       return weather
    }
}