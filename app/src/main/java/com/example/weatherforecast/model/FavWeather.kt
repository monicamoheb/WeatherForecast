package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.io.Serializable

@Entity(tableName = "FavWeatherTable")
data class FavWeather (
    @PrimaryKey
    var id:Int,
    var currentTemp:Double,
    var currentDate: Date,
    var humidity:Int,
    var windSpeed:Double,
    var pressure:Int,
    var clouds:Int,
    var cityName:String,
    var icon: String,
    var weatherDesc: String,
    var pastHourly: List<Hourly>,
    var pastDaily: List<Daily>
):Serializable