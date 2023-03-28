package com.example.weatherforecast.home.view

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.HoursWeatherItemBinding
import com.example.weatherforecast.model.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(var hList: List<Hourly>) : RecyclerView.Adapter<HourlyAdapter.MyViewHolder>() {
    lateinit var binding: HoursWeatherItemBinding
    class MyViewHolder(var binding: HoursWeatherItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HoursWeatherItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return hList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentHourly= hList.get(position)

        val converter=-272.15
        holder.binding.tvDegreeWeatherItemHours.text=(currentHourly.temp+converter).toInt().toString()

        holder.binding.tvHourWeatherItem.text=
            if(currentHourly==hList[0])
                "Now"
            else
                getHour(currentHourly.dt.toString())

        Glide.with(holder.binding.imageViewWeatherStatusPerHours.context)
            .load("https://openweathermap.org/img/wn/${currentHourly.weather.get(0).icon}@2x.png")
            .into(holder.binding.imageViewWeatherStatusPerHours)
    }
    private fun getHour(hour: String):String{
        return try {
            val simpleDateFormat=SimpleDateFormat("h a")
            val netData = Date(hour.toLong()*1000)
            simpleDateFormat.format(netData)
        }catch (ex:Exception){
            ex.toString()
        }
    }
}