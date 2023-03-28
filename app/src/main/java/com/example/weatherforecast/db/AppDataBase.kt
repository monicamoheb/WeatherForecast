package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.converters.Converters
import com.example.weatherforecast.converters.DailyConverter
import com.example.weatherforecast.converters.HourlyConverter
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.WeatherResponse


@Database(entities = [WeatherResponse::class,FavWeather::class,AlertModel::class], version = 1)
@TypeConverters(Converters::class,HourlyConverter::class,DailyConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao

    companion object {
        private var instance: AppDataBase? = null

        @Synchronized
        fun getInstance(context: Context): AppDataBase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java, "weather")
                    .build()
            }
            return instance
        }
    }
}
