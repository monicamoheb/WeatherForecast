package com.example.weatherforecast

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class MyAlertDialog {
    companion object {
        fun myDialog(context: Context): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert !")
            builder.setCancelable(false)
            return builder
        }
    }
}