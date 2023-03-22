package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.converters.Converters
import com.example.weatherforecast.converters.DailyConverter
import com.example.weatherforecast.converters.HourlyConverter
import com.example.weatherforecast.converters.MinutelyConverter
import com.example.weatherforecast.model.WeatherResponse


@Database(entities = [WeatherResponse::class], version = 1)
@TypeConverters(Converters::class,HourlyConverter::class,DailyConverter::class,MinutelyConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun favLocationsDao(): FavLocationsDao

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
