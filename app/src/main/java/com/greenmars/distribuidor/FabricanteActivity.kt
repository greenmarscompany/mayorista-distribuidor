package com.greenmars.distribuidor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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