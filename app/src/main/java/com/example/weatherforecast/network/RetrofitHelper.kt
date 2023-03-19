package com.example.weatherforecast.network

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/"

object RetrofitHelper {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .build()

    fun getLocation(): LocationService = retrofit.create(LocationService::class.java)
}