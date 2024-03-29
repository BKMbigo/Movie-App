package com.bkmbigo.movieapp.ui.main

import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.databinding.ActivityMainBinding
import com.bkmbigo.movieapp.ui.main.search.SearchFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        handleDeepLinkIntent()

        val navView: NavigationView = binding.navView
        val navHostController = supportFragmentManager.findFragmentById(binding.contentMain.navHostFragmentContentMain.id) as NavHostFragment

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.HomeFragment, R.id.SearchFragment
            ),
            binding.root)
        setupActionBarWithNavController(navHostController.navController, appBarConfiguration)
        navView.setupWithNavController(navHostController.navController)

    }

    private fun handleDeepLinkIntent(){

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostController = supportFragmentManager.findFragmentById(binding.contentMain.navHostFragmentContentMain.id) as NavHostFragment
        return navHostController.navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}