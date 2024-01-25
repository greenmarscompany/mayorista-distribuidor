package com.greenmars.distribuidor.ui.store

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.database.DatabaseHelper
import com.greenmars.distribuidor.databinding.ActivityStoreBinding
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.model.Account
import com.greenmars.distribuidor.ui.store.cart.CartFragment
import com.greenmars.distribuidor.ui.store.company.StoreCompanyFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreActivity : AppCompatActivity() {

    private val storeActivityViewModel by viewModels<StoreActivityViewModel>()

    private lateinit var binding: ActivityStoreBinding
    private lateinit var db: DatabaseHelper
    private lateinit var badge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentInit = StoreCompanyFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragments, fragmentInit)
            .commit()

        db = DatabaseHelper(applicationContext)
        badge = BadgeDrawable.create(this)
        menuBadge()
        initUI()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentInits = StoreCompanyFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.containerFragments, fragmentInits)
                    .addToBackStack(null)
                    .commit()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initUI() {
        val account: Account = db.acountToken
        val store = CartStore(
            id = 0,
            clientId = account.company_id
        )



        storeActivityViewModel.getCart()
        storeActivityViewModel.saveCart(store)
        storeActivityViewModel.getCountItem()

        initState()
        menusListeners()


    }


    private fun initState() {



        lifecycleScope.launch {

            storeActivityViewModel.counter.collect {
                Log.i("counter", "Cantidad Activity: ${it.toString()}")
                badge.number = it
                badge.isVisible = it > 0
                Log.i("counter", "isVisible: ${badge.isVisible}")
            }

            /*Log.i("activity", "Antes de repeatOnLifecycle")
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.i("activity", "Dentro de repeatOnLifecycle")
                launch {
                    storeActivityViewModel.carts.collect {
                    }
                }

                launch {

                }

            }*/
        }
    }

    private fun menusListeners() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.imCart -> {
                    val fragmentInit = CartFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.containerFragments, fragmentInit)
                        .commit()

                    true
                }

                else -> false
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun menuBadge() {
        BadgeUtils.attachBadgeDrawable(badge, binding.topAppBar, R.id.imCart)
    }
}