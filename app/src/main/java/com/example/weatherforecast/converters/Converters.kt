package com.example.weatherforecast.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Current
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.model.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCurrentToString(current: Current):String{
        return Gson().toJson(current)
    }
    @TypeConverter
    fun fromStringToCurrent(strObject: String):Current{
        val list= object: TypeToken<Current>(){}.type
        return Gson().fromJson(strObject, list)
    }
}