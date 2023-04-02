package com.example.weatherforecast

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.databinding.InitialSetupBinding
import com.example.weatherforecast.model.FavWeather
import com.example.weatherforecast.model.SettingsModel
import com.google.android.material.navigation.NavigationView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var mySharedPref: MySharedPref
    lateinit var setting: SettingsModel

    private val navController by lazy {
        findNavController(this, R.id.nav_host_fragment)
    }

    private companion object {
        var fragmentID: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mySharedPref = MySharedPref(this)
        setting = mySharedPref.sharedPrefRead()
        Log.e(TAG, "onCreate: $setting")
        if (setting.location.equals("not found") || setting.notification.equals("not found")) {
            showInitialSetupDialog()
        }
        initUI()
    }

    fun initUI() {
        val drawerLayout: DrawerLayout = binding.drawerLayoutMain
        val navView: NavigationView = binding.navViewMain

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.favoriteFragment, R.id.alertsFragment, R.id.settingsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        fragmentID = navController.currentDestination?.id as Int

        navController.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                fragmentID = destination.id
            }
        })
    }

    private fun showInitialSetupDialog() {
        val builder: AlertDialog.Builder = MyAlertDialog.myDialog(this)
        builder.setTitle("Initial Setup")
        builder.setIcon(R.drawable.settings)
        builder.setView(R.layout.initial_setup)
        val view = InitialSetupBinding.inflate(layoutInflater)
        val gps = view.gpsRadioButton
        val map = view.mapRadioButton
        val enabled = view.enableRadioButton
        val disable = view.disableRadioButton

        builder.setPositiveButton("ok",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                if (gps.isChecked || map.isChecked) {
                    if (enabled.isChecked || disable.isChecked) {
                        val location: String = if (gps.isChecked) "gps" else "map"
                        val notification: String = if (enabled.isChecked) "enable" else "disable"
                        setting = SettingsModel(location, notification)
                        mySharedPref.sharedPrefWrite(setting)
                        dialog.cancel()
                    }
                }

            })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayoutMain.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}