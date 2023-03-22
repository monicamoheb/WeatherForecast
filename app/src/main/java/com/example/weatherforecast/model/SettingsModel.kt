package com.example.weatherforecast.model

data class SettingsModel (
    var location:String,
    var notification:String,
    var lang:String="en",
    var windSpeed:String="meter/sec",
    var temp: String="celsius"
)