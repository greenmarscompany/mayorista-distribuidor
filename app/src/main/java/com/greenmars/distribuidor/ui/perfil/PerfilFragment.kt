package com.greenmars.distribuidor.ui.perfil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.greenmars.distribuidor.database.DatabaseHelper
import com.greenmars.distribuidor.databinding.FragmentPerfilBinding
import com.greenmars.distribuidor.model.Account
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val perfilViewModel: PerfilViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPerfilBinding.inflate(layoutInflater, container, false)
        initUI()

        return binding.root
    }

    private fun initUI() {
        logout()
    }

    private fun logout() {
        val dataBase = DatabaseHelper(context)

        binding.lyLogout.setOnClickListener {
            dataBase.clearToken()
            perfilViewModel.logoutStaff()
            activity?.finish()
        }

    }
}