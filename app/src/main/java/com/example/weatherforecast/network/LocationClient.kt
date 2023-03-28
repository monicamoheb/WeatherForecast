package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class LocationClient private constructor() : RemoteSource {
    val services: LocationService by lazy {
        RetrofitHelper.getLocation()
    }

    override suspend fun getLocationOnline(lat: String, lon: String): WeatherResponse {
        return services.getWeather(lat,lon)
    }

    companion object {
        private var instance: LocationClient? = null
        fun getInstance(): LocationClient {
            return instance ?: synchronized(this) {
                val temp = LocationClient()
                instance = temp
                temp
            }
        }
    }

}