package com.example.weatherforecast.home.view


import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.DaysWeatherItemBinding
import com.example.weatherforecast.databinding.HoursWeatherItemBinding
import com.example.weatherforecast.model.Daily
import com.example.weatherforecast.model.Hourly
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days

private const val TAG = "DailyAdapter"
class DailyAdapter(var dList: List<Daily>) : RecyclerView.Adapter<DailyAdapter.MyViewHolder>() {
    lateinit var binding: DaysWeatherItemBinding
    class MyViewHolder(var binding: DaysWeatherItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DaysWeatherItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentDay= dList.get(position)

        holder.binding.tvDegreeWeatherItemDays.text=((currentDay.temp.max - 273.15).toFloat().toString()+" / "+(currentDay.temp.min- 273.15).toFloat().toString())

        holder.binding.tvStatusWeatherItemDays.text=currentDay.weather.get(0).description

        holder.binding.tvDayWeatherItem.text=
            if(currentDay==dList[0])
                "Tomorrow"
            else
                getDateTime(currentDay.dt.toString())

        Log.e(TAG, "onBindViewHolder: $currentDay.weather.get(0).icon" )
        Glide.with(holder.binding.imageViewWeatherStatusPerDays.context)
            .load("https://openweathermap.org/img/wn/${currentDay.weather.get(0).icon}@2x.png")
            .into(holder.binding.imageViewWeatherStatusPerDays)
    }
    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}