package com.greenmars.distribuidor.ui.perfil

import android.content.Context
import android.content.SharedPreferences
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

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPerfilBinding.inflate(layoutInflater, container, false)
        initUI()

        /*val db = DatabaseHelper(context)
        val account: Account = db.acountToken
        if (account != null) {
            llenarDatos(account)
        }*/

        val sharedPreferences =
            requireContext().getSharedPreferences("mi_pref", Context.MODE_PRIVATE)
        val userid = sharedPreferences.getLong("iduser", 0L)

        perfilViewModel.getUser(userid)

        return binding.root
    }

    private fun initUI() {
        logout()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                perfilViewModel.user.collect {
                    if (it != null) {
                        binding.apply {

                            tvName.text = it.companyName
                            tvPhone.text = it.companyPhone
                            tvRuc.text = it.companyRuc
                            tvAddress.text = it.companyAddress
                        }

                    }
                }
            }
        }
    }

    private fun logout() {
        val dataBase = DatabaseHelper(context)
        sharedPreferences = requireContext().getSharedPreferences("mi_pref", Context.MODE_PRIVATE)
        binding.lyLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putLong("iduser", 0)
            editor.putString("token", "")
            editor.apply()

            dataBase.clearToken()
            dataBase.deleteLogin()
            perfilViewModel.logoutStaff()
            activity?.finish()
        }

    }

    private fun llenarDatos(account: Account) {
        binding.apply {
            tvName.text = account.nombre
            tvPhone.text = account.telefono
            tvRuc.text = account.company_ruc
            tvAddress.text = account.direccion
        }
    }
}