package com.example.weatherforecast.repo

import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.LocationClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    override suspend fun getCurrentWeatherOnline(lat: String, lon: String): Flow<WeatherResponse> {
        return  flowOf( remoteSource.getLocationOnline(lat, lon))
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        localSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun deleteCurrentWeather() {
        localSource.deleteCurrentWeather()
    }

    override suspend fun getCurrentWeatherDB(): Flow<WeatherResponse> {
        return localSource.getCurrentWeather()
    }

    override suspend fun insertFavLocation(favWeather: FavWeather) {
        localSource.insertFavLocation(favWeather)
    }

    override suspend fun getFavLocations(): Flow<List<FavWeather>> {
       return localSource.getFavLocations()
    }

    override suspend fun deleteFavLocation(favWeather: FavWeather) {
        localSource.deleteFavLocation(favWeather)
    }

}