package com.example.weatherforecast.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class HourlyConverter {
    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>):String{
        return Gson().toJson(hourly)
    }
    @TypeConverter
    fun fromStringToHourly(strObject: String): List<Hourly> {
        val list= object:TypeToken<List<Hourly>>(){}.type
        return Gson().fromJson(strObject, list)
    }
}