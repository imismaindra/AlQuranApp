package com.ismaindra.alquranapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ismaindra.alquranapp.databinding.ActivityMainBinding
import com.ismaindra.alquranapp.R
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocation: FusedLocationProviderClient

//    private val locationPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
//            val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
//                    perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
//
//            if (granted) {
//                getCurrentLocation()
//            }
//        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
//        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
//        requestLocationPermission()

//        setupToolbar()
        setupNavigation()
    }

//    private fun requestLocationPermission() {
//        locationPermissionLauncher.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        )
//    }

//    @SuppressLint("MissingPermission")
//    private fun getCurrentLocation() {
//        fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//            .addOnSuccessListener { location ->
//                if (location != null) {
//                    println("Lat: ${location.latitude}, Lon: ${location.longitude}")
//                }
//            }
//    }

//    private fun setupToolbar() {
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.apply {
//            title = "Al-Quran Digital"
//        }
//    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom navigation dengan navigation component
        binding.bottomNavigation.setupWithNavController(navController)

        // Handle item selection manually jika diperlukan
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_surah -> {
                    navController.navigate(R.id.surahListFragment)
                    true
                }
                R.id.nav_bookmark -> {
//                     Navigate to bookmark fragment
                     navController.navigate(R.id.bookmarkFragment)
                    true
                }
                else -> false
            }
        }
    }
}