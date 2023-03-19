package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import retrofit2.Response

class LocationClient private constructor() : RemoteSource {
    val services: LocationService by lazy {
        RetrofitHelper.getLocation()
    }

    override suspend fun getLocationOnline(lat: String, lon: String): Response<WeatherResponse> {
        return services.getWeather(lat,lon)
    }

    companion object {
        private var instance: RemoteSource? = null
        fun getInstance(): RemoteSource {
            return instance ?: synchronized(this) {
                val temp = LocationClient()
                instance = temp
                temp
            }
        }
    }




}