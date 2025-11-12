package com.ismaindra.alquranapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ismaindra.alquranapp.databinding.ActivityMainBinding
import com.ismaindra.alquranapp.R
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Al-Quran Digital"
        }
    }

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