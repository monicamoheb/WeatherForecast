package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=weekly&appid=662e20775c1af4fcf9dd9b029978fb3e
const val API_KEY="662e20775c1af4fcf9dd9b029978fb3e"
interface LocationService {
    @GET("data/2.5/onecall")
    suspend fun getWeather(@Query("lat") lat: String ,@Query("lon") lon: String,
                           @Query("appid") appIp: String= API_KEY,@Query("lang") lang: String= "en"): Flow<WeatherResponse>
    //@Query("q") cityName: String
}