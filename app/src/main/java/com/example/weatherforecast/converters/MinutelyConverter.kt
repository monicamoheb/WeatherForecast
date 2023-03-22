package com.example.weatherforecast.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.model.Minutely
import com.example.weatherforecast.model.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MinutelyConverter {
    @TypeConverter
    fun fromMinutelyToString(minutely: List<Minutely>):String{
        return Gson().toJson(minutely)
    }
    @TypeConverter
    fun fromStringToMinutely(strObject: String): List<Minutely> {
        val list= object: TypeToken<List<Minutely>>(){}.type
        return Gson().fromJson(strObject, list)
    }
}