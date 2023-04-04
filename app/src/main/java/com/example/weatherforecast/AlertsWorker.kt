package com.example.weatherforecast

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.model.SettingsModel
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.network.RetrofitHelper
import com.example.weatherforecast.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val CHANNEL_ID="123"
class AlertsWorker(private var context: Context, private var workParams: WorkerParameters) :
    CoroutineWorker(context, workParams) {

    lateinit var mySharedPref: MySharedPref
    lateinit var setting: SettingsModel
    lateinit var repo: Repo

    override suspend fun doWork(): Result {
        repo=Repo.getInstance(
            LocationClient.getInstance(),
            ConcreteLocalSource(context)
        )

        try {
            val type=inputData.getString("alertType")
            val weather=repo.getCurrentWeatherDBForWorker()
            val apiInstance = RetrofitHelper.getLocation()
            val resultAlert = apiInstance.getWeather(weather.lat.toString(), weather.lon.toString(), "en", "standard")
            if (resultAlert.isSuccessful) {
                val alert = resultAlert.body()?.alerts?.get(0)
                var desc=""

                if (alert == null) {
                    desc="Everything is good today"
                } else {
                    val alertDesc=alert.description.split("...")
                  desc=if (alertDesc[0].isBlank()) alertDesc[1] else alertDesc[0]
                }
                mySharedPref = MySharedPref(context)
                setting= mySharedPref.sharedPrefRead()

                if (type=="notification"){
                    if(setting.notification=="enable") {
                        createNotification(desc)
                    }
                }
                else{
                    createAlarm(desc)
                }

                return Result.success()

            } else {
                return Result.failure(workDataOf(Constants.FAILURE_REASON to resultAlert.errorBody()))
            }
        } catch (e: Exception) {
            return Result.failure(workDataOf(Constants.FAILURE_REASON to e.message))
        }
    }
    private suspend fun createAlarm( contentText: String) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_ringtone)
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val view: View = LayoutInflater.from(context).inflate(R.layout.alarm_layout, null)
        val btnDismiss = view.findViewById<Button>(R.id.dismiss_btn)
        val tvDesc = view.findViewById<TextView>(R.id.alarm_layout_desc_tv)


        val layoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                flag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        layoutParams.gravity = Gravity.TOP

        var windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            tvDesc.text = contentText
        }

        mediaPlayer.start()
        mediaPlayer.isLooping = true
        btnDismiss.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
    }


    private fun createNotification(desc:String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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