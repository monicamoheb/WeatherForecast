package com.example.weatherforecast

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayoutMain
        val navView: NavigationView = binding.navViewMain

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.favoriteFragment, R.id.alertsFragment,R.id.settingsFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
        if(item.itemId==android.R.id.home){
            if (binding.drawerLayoutMain.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
            }else{
                binding.drawerLayoutMain.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}