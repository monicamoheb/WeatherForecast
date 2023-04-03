package com.example.weatherforecast.alerts.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.MyAlertDialog
import com.example.weatherforecast.R
import com.example.weatherforecast.alerts.viewmodel.AlertsViewModel
import com.example.weatherforecast.alerts.viewmodel.AlertsViewModelFactory
import com.example.weatherforecast.databinding.AlertSetupDialogBinding
import com.example.weatherforecast.databinding.FragmentAlertsBinding
import com.example.weatherforecast.db.ConcreteLocalSource
import com.example.weatherforecast.model.AlertModel
import com.example.weatherforecast.network.LocationClient
import com.example.weatherforecast.repo.Repo
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

private const val TAG = "AlertsFragment"
class AlertsFragment : Fragment(),OnAlertsClickListener {

    lateinit var binding: FragmentAlertsBinding
    lateinit var alertsViewModel: AlertsViewModel
    lateinit var alertsViewModelFactory: AlertsViewModelFactory
    lateinit var alertsAdapter: AlertsAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var timePicker: MaterialTimePicker
    lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_alerts, container, false)
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            showAlertSetupDialog()
        }

        alertsViewModelFactory= AlertsViewModelFactory(
            Repo.getInstance(
                LocationClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )

        alertsViewModel= ViewModelProvider(this, alertsViewModelFactory).get(AlertsViewModel::class.java)

        setUpRecyclerView()
        getData()
    }

    private fun showAlertSetupDialog() {
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.alert_setup_dialog)
        val startDay=dialog.findViewById<TextView>(R.id.startDay)
        val endDay=dialog.findViewById<TextView>(R.id.endDay)
        val startHour=dialog.findViewById<TextView>(R.id.startHour)
        val endHour=dialog.findViewById<TextView>(R.id.endHour)
        val notificationRadioButton=dialog.findViewById<RadioButton>(R.id.notification_radioButton)
        val alarmRadioButton=dialog.findViewById<RadioButton>(R.id.alarm_radioButton)
        val save_btn=dialog.findViewById<Button>(R.id.saveAlertDialog_btn)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

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

        save_btn.setOnClickListener{
            if (notificationRadioButton.isChecked || alarmRadioButton.isChecked) {
                if (startHour.text != "Hour" && endHour.text != "Hour") {
                    if (startDay.text != "Day" && endDay.text != "Day") {
                        val alertType =
                            if (notificationRadioButton.isChecked) "notification" else "alarm"
                        val alert = AlertModel(
                            startDay.text.toString(),
                            endDay.text.toString(),
                            startHour.text.toString(),
                            alertType
                        )
                        alertsViewModel.insertAlert(alert)
                        dialog.dismiss()
                    }
                    else{
                        Toast.makeText(requireContext(), "Please enter start day and end day", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "Please enter start hour and end hour", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDatePicker(textView: TextView){
        val datePicker=MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
            val date = dateFormatter.format(Date(it))
            textView.text=date
            Toast.makeText(requireContext(), "$date is selected", Toast.LENGTH_LONG).show()
        }

        // Setting up the event for when cancelled is clicked
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(requireContext(), "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
        }

        // Setting up the event for when back button is pressed
        datePicker.addOnCancelListener {
            Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG).show()
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
            if (timePicker.hour > 12){
                textView.text = String.format("%02d", (timePicker.hour - 12)) + " : " + String.format("%02d", (timePicker.minute)) + "PM"
            }
            else{
                textView.text = "${timePicker.hour} : ${timePicker.minute} AM"
            }
            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

        }
    }

    private fun setUpRecyclerView(){
        layoutManager= LinearLayoutManager(requireContext())
        layoutManager.orientation= RecyclerView.VERTICAL
        alertsAdapter= AlertsAdapter(ArrayList(),this,requireContext())
        binding.AlertsRecyclerView.adapter=alertsAdapter
        binding.AlertsRecyclerView.layoutManager=layoutManager
    }

    private fun getData(){
        Log.e(TAG, "getData: ${alertsViewModel.stateFlow.value}")
        lifecycleScope.launch(Dispatchers.IO) {
            alertsViewModel.stateFlow.collectLatest { favList ->
                alertsAdapter.AList= favList
                withContext(Dispatchers.Main){
                    alertsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(alert: AlertModel) {
        alertsViewModel.deleteAlert(alert)
        Toast.makeText(requireContext(),"Removed from alerts list ..", Toast.LENGTH_LONG).show()
    }


}