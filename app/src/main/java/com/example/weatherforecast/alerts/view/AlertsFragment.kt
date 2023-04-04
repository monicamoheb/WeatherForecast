package com.example.weatherforecast.alerts.view

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.alerts.viewmodel.AlertsViewModel
import com.example.weatherforecast.alerts.viewmodel.AlertsViewModelFactory
import com.example.weatherforecast.databinding.FragmentAlertsBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherforecast.*
import com.example.weatherforecast.db.AppDataBase
import com.example.weatherforecast.db.CurrentWeatherDao
import com.example.weatherforecast.home.viewmodel.HomeViewModel
import com.example.weatherforecast.home.viewmodel.HomeViewModelFactory
import com.example.weatherforecast.model.SettingsModel
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

private const val TAG = "AlertsFragment"

class AlertsFragment : Fragment(), OnAlertsClickListener {

    lateinit var binding: FragmentAlertsBinding
    private lateinit var alertsViewModel: AlertsViewModel
    lateinit var alertsViewModelFactory: AlertsViewModelFactory
    lateinit var alertsAdapter: AlertsAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var timePicker: MaterialTimePicker
    private lateinit var fullFormat: SimpleDateFormat
    private lateinit var fullStartDate: Date
    private lateinit var fullEndDate: Date
    private lateinit var saveBtn: Button
    lateinit var startDay: TextView
    lateinit var endDay: TextView
    lateinit var startHour: TextView
    lateinit var endHour: TextView
    lateinit var alarmRadioButton: RadioButton
    lateinit var notificationRadioButton: RadioButton
    lateinit var alertType: String
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_alerts, container, false)
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireActivity().applicationContext)
        val currentWeatherDao : CurrentWeatherDao by lazy {
            val db : AppDataBase = AppDataBase.getInstance(requireContext()) as AppDataBase
            db.currentWeatherDao()
        }
        alertsViewModelFactory = AlertsViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(currentWeatherDao)
            )
        )

        alertsViewModel =
            ViewModelProvider(this, alertsViewModelFactory)[AlertsViewModel::class.java]

        setUpRecyclerView()
        getData()

        showAlertSetupDialog()

    }

    private fun showAlertSetupDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.alert_setup_dialog)

        startDay = dialog.findViewById(R.id.startDay)
        endDay = dialog.findViewById(R.id.endDay)
        startHour = dialog.findViewById(R.id.startHour)
        endHour = dialog.findViewById(R.id.endHour)
        notificationRadioButton = dialog.findViewById(R.id.notification_radioButton)
        alarmRadioButton = dialog.findViewById(R.id.alarm_radioButton)
        saveBtn = dialog.findViewById(R.id.saveAlertDialog_btn)

        val myCalendar = Calendar.getInstance()

        binding.fab.setOnClickListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        var calendarDate = myCalendar.time
        val dayFormatter = SimpleDateFormat("dd MMM, yyyy ")
        val timeFormatter = SimpleDateFormat("HH:mm")
        startDay.text = dayFormatter.format(calendarDate)
        startHour.text = timeFormatter.format(calendarDate)

        fullFormat = SimpleDateFormat("dd MMM, yyyy HH:mm")

        fullStartDate = fullFormat.parse(startDay.text.toString() + startHour.text.toString())
        myCalendar.add(Calendar.DAY_OF_YEAR, 3)
        calendarDate = myCalendar.time
        endDay.text = dayFormatter.format(calendarDate)
        endHour.text = timeFormatter.format(calendarDate)

        fullEndDate = fullFormat.parse(endDay.text.toString() + endHour.text.toString())


        startHour.setOnClickListener {
            Log.e(TAG, "showAlertSetupDialog: staaaartttt hourr")
            showTimePicker(startHour)
        }
        endHour.setOnClickListener {
            Log.e(TAG, "showAlertSetupDialog: enddd hourr")
            showTimePicker(endHour)
        }

        startDay.setOnClickListener {
            showDatePicker(startDay)
        }

        endDay.setOnClickListener {
            showDatePicker(endDay)
        }

        saveBtn.setOnClickListener {
            if (notificationRadioButton.isChecked || alarmRadioButton.isChecked) {
                alertType =
                    if (notificationRadioButton.isChecked)
                        "notification"
                    else
                        "alarm"

                if (alertType == "alarm") {
                    if (Settings.canDrawOverlays(context)) {
                        addAlert()
                        dialog.dismiss()
                    } else {
                        checkDrawOverPermission()
                    }
                } else if (alertType == "notification") {

                    createNotificationChannel()
                    addAlert()
                    dialog.dismiss()

                }

            }
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "weather"
            val description = "weather alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = ContextCompat.getSystemService(
                requireContext(),
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun addAlert() {
        val id = startWorkManager(alertType)

        alertsViewModel.insertAlert(
            AlertModel(
                id.toString(),
                startDay.text.toString(),
                endDay.text.toString(),
                startHour.text.toString(),
                alertType
            )
        )
    }

    private fun startWorkManager(alertType: String): UUID {

        val request = PeriodicWorkRequestBuilder<AlertsWorker>(
            1, TimeUnit.DAYS
        )
            .addTag(Constants.AlertWorker_TAG)
            .setInputData(
                workDataOf(
                    "alertType" to alertType
                )
            )
            .setInitialDelay(
                fullStartDate.time - Calendar.getInstance().timeInMillis,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueue(
            request
        )

        workManager.getWorkInfosByTagLiveData(Constants.AlertWorker_TAG)
            .observe(requireActivity()) { workInfos ->
                val myInfos = workInfos?.find { it.id == request.id }
                when (myInfos?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Log.i(TAG, "SUCCEEDED")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.i(TAG, "RUNNING")
                    }
                    WorkInfo.State.FAILED -> {
                        val reason = myInfos.outputData.getString(Constants.FAILURE_REASON)
                        Log.i(TAG, "FAILED $reason")
                    }
                    else -> {
                        Log.i(TAG, "another state .... ${myInfos?.state}")
                    }
                }
            }
        return request.id
    }

    private fun showDatePicker(textView: TextView) {
        val picker = MaterialDatePicker.Builder.datePicker()
        val dateValidator = DateValidatorPointForward.now()
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(dateValidator)
        picker.setCalendarConstraints(constraintsBuilder.build())
        val datePicker = picker.build()
        datePicker.show(requireActivity().supportFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            textView.text = date
            Toast.makeText(requireContext(), "$date is selected", Toast.LENGTH_LONG).show()
        }

        // Setting up the event for when cancelled is clicked
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(
                requireContext(),
                "${datePicker.headerText} is cancelled",
                Toast.LENGTH_LONG
            ).show()
        }

        // Setting up the event for when back button is pressed
        datePicker.addOnCancelListener {
            Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
        datePicker.addOnDismissListener {
            validateDateStartBeforeEnd()
        }
    }

    private fun showTimePicker(textView: TextView) {

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        timePicker.show(requireActivity().supportFragmentManager, "alertsChannel")
        timePicker.addOnPositiveButtonClickListener {
            textView.text = "${timePicker.hour}:${timePicker.minute}"
        }
        timePicker.addOnDismissListener {
            validateDateStartBeforeEnd()
        }
    }

    private fun validateDateStartBeforeEnd() {
        fullEndDate =
            fullFormat.parse(endDay.text.toString() + " " + endHour.text.toString()) as Date

        fullStartDate =
            fullFormat.parse(startDay.text.toString() + " " + startHour.text.toString()) as Date
        if (fullEndDate.time < fullStartDate.time) {
            Toast.makeText(
                requireContext(),
                "Invalid time!",
                Toast.LENGTH_LONG
            ).show()
            saveBtn.isEnabled = false
        } else
            saveBtn.isEnabled = true
    }

    private fun setUpRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        alertsAdapter = AlertsAdapter(ArrayList(), this, requireContext())
        binding.AlertsRecyclerView.adapter = alertsAdapter
        binding.AlertsRecyclerView.layoutManager = layoutManager
    }

    private fun getData() {
        Log.e(TAG, "getData: ${alertsViewModel.stateFlow.value}")
        lifecycleScope.launch(Dispatchers.IO) {
            alertsViewModel.stateFlow.collectLatest { result ->
                when (result) {
                    is AlertApiState.Loading -> {
                    }
                    is AlertApiState.Success -> {
                        alertsAdapter.AList = result.alertData
                        withContext(Dispatchers.Main) {
                            alertsAdapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Failed to fetch ..", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    override fun onClick(alert: AlertModel) {
        alertsViewModel.deleteAlert(alert)
        workManager.cancelWorkById(UUID.fromString(alert.id))
        Toast.makeText(requireContext(), "Removed from alerts list ..", Toast.LENGTH_LONG).show()
    }

    private fun checkDrawOverPermission() {
        if (!Settings.canDrawOverlays(context)) {
            val drawOverPermissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireActivity().packageName)
            )
            startActivityForResult(drawOverPermissionIntent, 4000)
        }
    }


}