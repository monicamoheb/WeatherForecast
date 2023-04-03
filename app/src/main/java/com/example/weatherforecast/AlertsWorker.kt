package com.example.weatherforecast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.network.RetrofitHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class AlertsWorker(private var context: Context, private var workParams: WorkerParameters) :
    CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        try {
            val apiInstance = RetrofitHelper.getLocation()
            val resultAlert = apiInstance.getWeather("", "", "", "")
            if (resultAlert.isSuccessful) {
                var alert = resultAlert.body()?.alerts?.get(0)
                var desc:String

                if (alert == null) {
                    desc="Everything is good today"
                } else {
                    desc=alert.description
                }

                createNotificationChannel()
                createNotification(desc)

                return Result.success()

            } else {
                return Result.failure(workDataOf(Constants.FAILURE_REASON to resultAlert.errorBody()))
            }
        } catch (e: Exception) {
            return Result.failure(workDataOf(Constants.FAILURE_REASON to e.message))
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "weather"
            val description: String = "weather alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun createNotification(desc:String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("Alert Notification")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(123, builder.build())
    }
}