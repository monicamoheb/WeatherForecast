package com.example.weatherforecast.favlocations.view

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.MyAlertDialog
import com.example.weatherforecast.NetworkChecker
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FavItemBinding
import com.example.weatherforecast.favlocations.view.FavoriteFragmentDirections.ActionFavoriteFragmentToHomeFragment
import com.example.weatherforecast.favlocations.view.FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment
import com.example.weatherforecast.model.FavWeather
import com.google.android.material.snackbar.Snackbar

class FavLocationAdapter(var vList:List<FavWeather>,var onFavClickListener: OnFavClickListener,var context: Context) :
    RecyclerView.Adapter<FavLocationAdapter.MyViewHolder>(){
    lateinit var binding: FavItemBinding
    class MyViewHolder(var binding: FavItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentFavWeather = vList.get(position)

        holder.binding.favItemName.text=currentFavWeather.timezone
        holder.binding.deleteFromFav.setOnClickListener{
           deleteFavWeather(currentFavWeather)
        }

        holder.binding.favItemName.setOnClickListener {
            val networkAvailability = NetworkChecker.isOnline(context)

            if (networkAvailability) {
                var action: ActionFavoriteFragmentToHomeFragment = actionFavoriteFragmentToHomeFragment()
                action.latlang = "${currentFavWeather.lat}+${currentFavWeather.lon}"
                Navigation.findNavController(holder.binding.root).navigate(action)
                action.latlang = null
            }
            else{
                Snackbar.make(binding.root,
                    "Check Your Connection ..",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun deleteFavWeather(weather: FavWeather) {
        val builder: AlertDialog.Builder = MyAlertDialog.myDialog(context)
        builder.setMessage("Do you want to remove this weather from favorites?")
        builder.setIcon(R.drawable.baseline_delete_24)
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                onFavClickListener.onClick(weather)
            })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() })
        val alertDialog = builder.create()
        alertDialog.show()
    }

}