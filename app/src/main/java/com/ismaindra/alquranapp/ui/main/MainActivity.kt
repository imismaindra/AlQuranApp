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

            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()

            navController.navigate(R.id.loginFragment, null, navOptions)

            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }


    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

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
                    navController.navigate(R.id.bookmarkFragment)
                    true
                }
                R.id.nav_users -> {
                    if (authManager.isLoggedIn()) {
                        navController.navigate(R.id.profileFragment)
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
