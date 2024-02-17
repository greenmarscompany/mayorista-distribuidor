package com.greenmars.distribuidor

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.greenmars.distribuidor.databinding.ActivityFabricanteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FabricanteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFabricanteBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFabricanteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        Log.i("fabriacti", "token: ${shared.getString("token", "")}")

        initUI()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

}