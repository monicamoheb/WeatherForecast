package com.example.weatherforecast.repo

import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.LocationClient
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repo private constructor(
    var remoteSource: LocationClient,
    var localSource: ConcreteLocalSource
) : RepoInterface {

    companion object {
        private var instance: Repo? = null
        fun getInstance(remoteSource: LocationClient, localSource: ConcreteLocalSource): Repo {
            return instance ?: synchronized(this) {
                val temp = Repo(remoteSource, localSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeatherOnline(lat: String, lon: String): Response<WeatherResponse> {
        return  remoteSource.getLocationOnline(lat, lon)
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        localSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun deleteCurrentWeather(weatherResponse: WeatherResponse) {
        localSource.deleteCurrentWeather(weatherResponse)
    }

    override suspend fun getCurrentWeatherDB(): Flow<WeatherResponse> {
        return localSource.getCurrentWeather()
    }

}