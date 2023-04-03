package com.example.weatherforecast.settings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.MySharedPref
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.model.SettingsModel
import java.util.*

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    lateinit var mySharedPref: MySharedPref
    lateinit var settings: SettingsModel
    lateinit var language: String
    lateinit var location: String
    lateinit var temp: String
    lateinit var windSpeed: String
    lateinit var notification: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mySharedPref=MySharedPref(requireContext())

        settings=mySharedPref.sharedPrefRead()
    }

    override fun onResume() {
        super.onResume()

        setupValuesToRadioButtons()
        setUpSharedPref()
        setUpSettings()
    }

    private fun setupValuesToRadioButtons(){
        if(settings.location == "gps") binding.GpsRadio.isChecked=true else binding.MapRadio.isChecked=true

        if(settings.lang == "en") binding.EnglishRadio.isChecked=true else binding.ArabicRadio.isChecked=true

        if(settings.temp == "metric") binding.CelsiusRadio.isChecked=true
        else if (settings.temp == "standard") binding.KelvinRadio.isChecked=true
        else binding.FahrenhietRadio.isChecked=true

        if(settings.windSpeed == "meter/sec") binding.MeterRadio.isChecked=true else binding.MileRadio.isChecked=true
        if(settings.notification == "enable") binding.EnableRadio.isChecked=true else binding.DisableRadio.isChecked=true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    fun setUpSettings(){
        binding.ArabicRadio.setOnClickListener {
            setLang("ar")
            setUpSharedPref()
        }
        binding.EnglishRadio.setOnClickListener {
            setLang("en")
            setUpSharedPref()
        }
        binding.GpsRadio.setOnClickListener {
            setUpSharedPref()
        }
        binding.MapRadio.setOnClickListener {
            setUpSharedPref()
        }

        binding.CelsiusRadio.setOnClickListener {
            setUpSharedPref()
        }
        binding.KelvinRadio.setOnClickListener {
            setUpSharedPref()
        }
        binding.FahrenhietRadio.setOnClickListener {
            setUpSharedPref()
        }

        binding.MeterRadio.setOnClickListener {
            setUpSharedPref()
        }
        binding.MileRadio.setOnClickListener {
            setUpSharedPref()
        }

        binding.EnableRadio.setOnClickListener {
            setUpSharedPref()
        }
        binding.DisableRadio.setOnClickListener {
            setUpSharedPref()
        }

    }

    private fun setUpSharedPref(){
        language=if(binding.ArabicRadio.isChecked) "ar" else "en"
        location=if(binding.GpsRadio.isChecked) "gps" else "map"
        temp= if(binding.KelvinRadio.isChecked) "standard" else if (binding.CelsiusRadio.isChecked) "metric" else "imperial"
        windSpeed= if(binding.MeterRadio.isChecked) "meter/sec" else "mile/hour"
        notification= if(binding.EnableRadio.isChecked) "enable" else "disable"

        settings=SettingsModel(location,notification,language,windSpeed,temp)
        mySharedPref.sharedPrefWrite(settings)
    }

    private fun setLang(lang:String) {
        val metric = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(lang)
        Locale.setDefault(Locale(lang))
        configuration.setLayoutDirection(Locale(lang))
        // update configuration
        resources.updateConfiguration(configuration, metric)
        // notify configuration
        onConfigurationChanged(configuration)
        requireActivity().recreate()
    }

}