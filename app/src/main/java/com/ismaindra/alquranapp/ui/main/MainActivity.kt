package com.ismaindra.alquranapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.databinding.ActivityMainBinding
import com.ismaindra.alquranapp.utils.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        authManager = AuthManager(this)

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (!authManager.isLoggedIn()) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // Cek jika saat ini bukan di login atau register, maka tendang ke login
            val currentDest = navController.currentDestination?.id
            if (currentDest != R.id.loginFragment && currentDest != R.id.registerFragment) {
                navController.navigate(R.id.loginFragment)
            }
        }
    }


    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Sekarang ini akan bekerja karena ID Menu == ID NavGraph
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
