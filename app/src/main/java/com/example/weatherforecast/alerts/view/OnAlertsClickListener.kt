package com.example.weatherforecast.alerts.view

import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.model.WeatherResponse

interface OnAlertsClickListener {
    fun onClick(alert: AlertModel)
}