package com.example.weatherforecast.db

import android.location.Address
import androidx.room.*
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)

    @Query("select * from WeatherTable")
    fun getCurrentWeather(): Flow<WeatherResponse>

    @Delete
    suspend fun deleteCurrentWeather(weatherResponse: WeatherResponse)
}