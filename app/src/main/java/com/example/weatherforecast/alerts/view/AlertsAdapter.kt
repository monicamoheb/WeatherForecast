package com.example.weatherforecast.alerts.view

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.MyAlertDialog
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.AlertItemBinding
import com.example.weatherforecast.model.AlertModel

class AlertsAdapter(
    var AList: List<AlertModel>,
    var onAlertsClickListener: OnAlertsClickListener,
    var context: Context
) :
    RecyclerView.Adapter<AlertsAdapter.MyViewHolder>() {

    lateinit var binding: AlertItemBinding

    class MyViewHolder(var binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return AList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentAlert = AList.get(position)

        holder.binding.favItemName.text = ""
        holder.binding.deleteFromAlerts.setOnClickListener {
            deleteAlert(currentAlert)
        }
    }

    private fun deleteAlert(alert: AlertModel) {
        val builder: AlertDialog.Builder = MyAlertDialog.myDialog(context)
        builder.setMessage("Do you want to remove this alert?")
        builder.setIcon(R.drawable.baseline_delete_24)
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                onAlertsClickListener.onClick(alert)
            })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() })
        val alertDialog = builder.create()
        alertDialog.show()
    }

}