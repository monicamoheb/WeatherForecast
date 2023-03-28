package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FavWeatherTable")
data class FavWeather (
    val lat: Double,
    val lon: Double,
    val timezone: String,
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}