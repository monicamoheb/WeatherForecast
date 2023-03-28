package com.example.weatherforecast.db

import android.location.Address
import androidx.room.*
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)

    @Query("select * from WeatherTable")
    fun getCurrentWeather(): Flow<WeatherResponse>

    @Query("DELETE FROM WeatherTable")
    suspend fun deleteCurrentWeather()

    // faaavvv
    @Insert(entity = FavWeather::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavLocation(favWeather: FavWeather)

    @Query("select * from FavWeatherTable")
    fun getFavLocations(): Flow<List<FavWeather>>

    @Delete(entity = FavWeather::class)
    suspend fun deleteFavLocation(favWeather: FavWeather)

//
//    //alerts
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAlert(alert: AlertModel)
//
//    @Query("select * from AlertsTable")
//    fun getAllAlerts(): Flow<List<AlertModel>>
//
//    @Delete
//    suspend fun deleteAlert(alert: AlertModel)
}