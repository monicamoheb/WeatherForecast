package com.example.weatherforecast

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class MyAlertDialog {
    companion object {
        fun myDialog(context: Context): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to exit ?")
            builder.setTitle("Alert !")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> })
            builder.setNegativeButton("No",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() })
            return builder
        }
    }
}