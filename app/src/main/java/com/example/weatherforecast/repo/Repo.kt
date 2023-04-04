package com.example.weatherforecast.repo

import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.LocationClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import retrofit2.Response

class Repo private constructor(
    var remoteSource: LocationClient,
    var localSource: ConcreteLocalSource,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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

    override suspend fun getCurrentWeatherOnline(lat: String, lon: String,lang:String,unit:String): Flow<WeatherResponse> {
        return  flowOf( remoteSource.getLocationOnline(lat, lon,lang,unit))
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        withContext(ioDispatcher) {
            localSource.insertCurrentWeather(weatherResponse)
        }
    }

    override suspend fun deleteCurrentWeather() {
        withContext(ioDispatcher) {
            localSource.deleteCurrentWeather()
        }
    }

    override suspend fun getCurrentWeatherDB(): Flow<WeatherResponse> {
        return localSource.getCurrentWeather()
    }

    override suspend fun getCurrentWeatherDBForWorker(): WeatherResponse {
        return localSource.getCurrentWeatherForWorker()
    }

    override suspend fun insertFavLocation(favWeather: FavWeather) {
        withContext(ioDispatcher) {
            localSource.insertFavLocation(favWeather)
        }
    }

    override suspend fun getFavLocations(): Flow<List<FavWeather>> {
       return localSource.getFavLocations()
    }

    override suspend fun deleteFavLocation(favWeather: FavWeather) {
        withContext(ioDispatcher) {
            localSource.deleteFavLocation(favWeather)
        }
    }

    override suspend fun insertAlert(alert: AlertModel) {
        withContext(ioDispatcher) {
            localSource.insertAlert(alert)
        }
    }

    override suspend fun getAllAlerts(): Flow<List<AlertModel>> {
        return localSource.getAllAlerts()
    }

    override suspend fun deleteAlert(alert: AlertModel) {
        withContext(ioDispatcher) {
            localSource.deleteAlert(alert)
        }
    }
}