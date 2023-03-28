package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherforecast.model.SettingsModel

class MySharedPref(var context: Context) {
    val PREF_NAME = "PREF"
    val LOCATION = "location"
    val LANGUAGE = "language"
    val TEMP = "temp"
    val WIND_SPEED = "wind speed"
    val NOTIFICATION = "notification"

    fun sharedPrefWrite(settings: SettingsModel) {
        val pref: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(LOCATION, settings.location)
        editor.putString(NOTIFICATION, settings.notification)
        editor.putString(LANGUAGE, settings.lang)
        editor.putString(TEMP, settings.temp)
        editor.putString(WIND_SPEED,settings.windSpeed)

        editor.commit()
    }

    fun sharedPrefRead():SettingsModel {

        val settings = SettingsModel("gps","enable")
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        settings.location=pref.getString(LOCATION, "not found") as String
        settings.notification=pref.getString(NOTIFICATION, "not found") as String
        settings.lang=pref.getString(LANGUAGE, "not found") as String
        settings.temp=pref.getString(TEMP, "not found") as String
        settings.windSpeed=pref.getString(WIND_SPEED, "not found") as String

        return settings
    }
}