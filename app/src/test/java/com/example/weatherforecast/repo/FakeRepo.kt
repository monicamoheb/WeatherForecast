package com.example.weatherforecast.repo

import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepo : RepoInterface {
    private val alertList= mutableListOf<AlertModel>()
    private val favList= mutableListOf<FavWeather>()
    private val current = Current(
        1, 1.2, 1, 2.1, 1, 1, 1, 1,
        2.2, 2.3, 1, listOf(), 1, 2.4, 2.5
    )
    private var weather: WeatherResponse = WeatherResponse(
        current, listOf(), listOf(), listOf(),
        1.1, 1.2, "", 1
    )

    override suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        unit: String
    ): Flow<WeatherResponse> {
        return flowOf(weather)
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
       weather=weatherResponse
    }

    override suspend fun deleteCurrentWeather() {

    }

    override suspend fun getCurrentWeatherDB(): Flow<WeatherResponse> {
        return flowOf(weather)
    }

    override suspend fun insertFavLocation(favWeather: FavWeather) {
        favList.add(favWeather)
    }

    override suspend fun getFavLocations(): Flow<List<FavWeather>> {
        return flowOf(favList)
    }

    override suspend fun deleteFavLocation(favWeather: FavWeather) {
        favList.remove(favWeather)
    }

    override suspend fun insertAlert(alert: AlertModel) {
        alertList.add(alert)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return flowOf(alertList)
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        alertList.remove(alert)
    }

    override suspend fun getCurrentWeatherDBForWorker(): WeatherResponse {
        return weather
    }
}