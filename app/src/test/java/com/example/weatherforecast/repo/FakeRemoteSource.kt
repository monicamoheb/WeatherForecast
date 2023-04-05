package com.example.weatherforecast.repo

import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.network.RemoteSource

class FakeRemoteSource(var weatherResponse: WeatherResponse) : RemoteSource{
    override suspend fun getLocationOnline(
        lat: String,
        lon: String,
        lang: String,
        unit: String
    ): WeatherResponse {
        return weatherResponse
    }
}