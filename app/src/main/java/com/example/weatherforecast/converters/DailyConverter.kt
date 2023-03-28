package com.example.weatherforecast.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Daily
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DailyConverter {
    @TypeConverter
    fun fromDailyToString(daily: List<Daily>):String{
        return Gson().toJson(daily)
    }
    @TypeConverter
    fun fromStringToDaily(strObject: String): List<Daily> {
        val list= object: TypeToken<List<Daily>>(){}.type
        return Gson().fromJson(strObject, list)
    }
}