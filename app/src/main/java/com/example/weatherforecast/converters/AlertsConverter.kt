package com.example.weatherforecast.converters

import androidx.room.TypeConverter
import com.example.weatherforecast.model.Alert
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlertsConverter {
    @TypeConverter
    fun fromAlertsToString(alerts: List<Alert>?):String?{
        return Gson().toJson(alerts)
    }
    @TypeConverter
    fun fromStringToAlerts(strObject: String?): List<Alert>? {
        val list= object: TypeToken<List<Alert>>(){}.type
        return Gson().fromJson(strObject, list)
    }
}