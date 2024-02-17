package com.greenmars.distribuidor.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import com.auth0.android.jwt.JWT
import com.google.firebase.messaging.FirebaseMessaging
import com.greenmars.distribuidor.FabricanteActivity
import com.greenmars.distribuidor.ForgotPassword_Fragment
import com.greenmars.distribuidor.HomeActivity
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.SignUp_Fragment
import com.greenmars.distribuidor.ValidarCuentaFragment
import com.greenmars.distribuidor.Variable
import com.greenmars.distribuidor.database.DatabaseHelper
import com.greenmars.distribuidor.databinding.FragmentIniciarSesionBinding
import com.greenmars.distribuidor.domain.RepositoryProveedor
import com.greenmars.distribuidor.domain.Staff
import com.greenmars.distribuidor.domain.UserDomain
import com.greenmars.distribuidor.model.Account
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Boolean
import javax.inject.Inject
import kotlin.Long
import kotlin.String
import kotlin.getValue
import kotlin.toString

@AndroidEntryPoint
class IniciarSesionFragment : Fragment() {
    private var _binding: FragmentIniciarSesionBinding? = null
    private val binding get() = _binding!!
    private val viewModelIniciar by viewModels<IniciarSesionViewModel>()

    private lateinit var fragmentManager: FragmentManager
    private lateinit var shakeAnimation: Animation
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: DatabaseHelper
    private var staffTemp: Staff? = null

    @Inject
    lateinit var repo: RepositoryProveedor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIniciarSesionBinding.inflate(layoutInflater, container, false)
        fragmentManager = requireActivity().supportFragmentManager

        db = DatabaseHelper(requireContext())


        var intent: Intent
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    // viewModelIniciar.loginS
                }

                /*launch {
                    viewModelIniciar.login.collect { loginDomain ->
                        Log.i("fragmt", "login domain: $loginDomain")
                        if (loginDomain != null) {
                            if (!loginDomain.success) {
                                Toast.makeText(
                                    requireContext(),
                                    "Credenciales incorrectas",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                val token = loginDomain.token
                                val jwt = JWT(token)
                                val useridJwt = jwt.getClaim("user_id").asLong()
                                val usernameJwt = jwt.getClaim("username").asString()

                                var username = ""
                                var userid: Long = 0L
                                if (usernameJwt != null) username = usernameJwt
                                if (useridJwt != null) userid = useridJwt
                                Log.i("fragmt", "username: $username, userid: $userid")
                                val editor = sharedPreferences.edit()
                                editor.putLong("iduser", userid)
                                editor.putString("token", token)
                                editor.apply()
                                viewModelIniciar.getStaff(userid.toInt())
                                updateTokenFire()
                            }
                        }
                    }
                }

                launch {
                    viewModelIniciar.staff.collect {
                        Log.i("viewmodel", "viewmodel staff: ${it}")
                        if (it != null) {
                            staffTemp = it
                        }
                        if (it != null) {
                            Log.i("fragment", "usuario staff online: $it")
                            viewModelIniciar.getUserOrInsert(it.userId.toLong(), it)
                            insertarUserLegacy(it, viewModelIniciar.login.value?.token ?: "")
                            intent = if (it.isSupplier == "1") {
                                Intent(context, FabricanteActivity::class.java)
                            } else {
                                Intent(context, HomeActivity::class.java)
                            }
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }

                launch {
                    viewModelIniciar.user.collect {
                        if (it != null) {
                            Log.i("fragment", "usuario room: $it")
                        }
                    }
                }

                launch {
                    viewModelIniciar.tokenFirebase.collect {
                        if (it != null) {
                            Log.i("fragment", "token firebase: $it")
                        }
                    }
                }*/
                launch {
                    viewModelIniciar.tokenFirebase.collect {
                        Log.i("fragment", "token firebase: $it")
                        if (it != null) {
                            Log.i("fragment", "token firebase: $it")
                        }
                    }
                }
            }
        }

        initUI()

        return binding.root
    }

    private fun initUI() {

        binding.progressBar.visibility = View.GONE
        shakeAnimation = AnimationUtils.loadAnimation(activity, R.anim.shake)
        binding.btnValidarCuenta.setOnClickListener { typeAction("validation") }
        binding.forgotPassword.setOnClickListener { typeAction("forgot") }
        binding.createAccount.setOnClickListener { typeAction("create") }
        binding.loginBtn.setOnClickListener { typeAction("ingresar") }

    }


    private fun typeAction(action: String) {

        when (action) {
            "ingresar" -> {
                checkValidation()
            }

            "forgot" -> {
                fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(
                        R.id.frameContainer,
                        ForgotPassword_Fragment(),
                        Variable.ForgotPassword_Fragment
                    )
                    .commit()
            }

            "validation" -> {
                fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(
                        R.id.frameContainer,
                        ValidarCuentaFragment(),
                        Variable.ForgotPassword_Fragment
                    )
                    .commit()
            }

            "create" -> {
                fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(R.id.frameContainer, SignUp_Fragment("t1"), Variable.SignUp_Fragment)
                    .commit()
            }
        }
    }

    private fun checkValidation() {
        val email = binding.tiEmail.editText?.text.toString()
        val password = binding.tiPassword.editText?.text.toString()

        if (email == "") binding.tiEmail.error = "Ingrese correo" else binding.tiEmail.error = null
        if (password == "") binding.tiPassword.error =
            "Ingrese su contraseña" else binding.tiEmail.error = null

        if (email != "" && password != "") {
            // viewModelIniciar.iniciarSession(email, password)
            val corutine = CoroutineScope(Dispatchers.Main)

            corutine.launch {
                sharedPreferences =
                    requireContext().getSharedPreferences("mi_pref", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val loginDomain = repo.iniciarSession(email, password)
                Log.i("fragment", "respuesta de logindomain: $loginDomain")
                if (loginDomain != null) {
                    val token = loginDomain.token
                    val jwt = JWT(token)
                    val useridJwt = jwt.getClaim("user_id").asLong()
                    val usernameJwt = jwt.getClaim("username").asString()

                    var username = ""
                    var userid: Long = 0L
                    if (usernameJwt != null) username = usernameJwt
                    if (useridJwt != null) userid = useridJwt
                    Log.i("fragmt", "username: $username, userid: $userid")

                    editor.putLong("iduser", userid)
                    editor.putString("token", token)
                    editor.apply()
                    val staff = repo.getStaff(userid.toInt())

                    if (staff != null) {
                        val userDomainEntity = UserDomain(
                            dni = "",
                            email = "",
                            telefono = staff.phone,
                            direccion = staff.address,
                            password = "",
                            token = token,
                            tipe = staff.type,
                            companyId = staff.companyId,
                            companyName = staff.nameCompany,
                            companyPhone = staff.phoneCompany,
                            companyAddress = staff.addressCompany,
                            companyLatitude = staff.latitude,
                            companyLongitude = staff.longitude,
                            companyRuc = staff.ruc,
                            nombre = staff.name,
                            isSupplier = staff.isSupplier,
                            cloudId = staff.userId.toLong()
                        )

                        insertarUserLegacy(staff, token)
                        val userDomain = repo.getUser(userid)
                        if (userDomain != null) {
                            Log.i("fragment", "Usuario room: $userDomain")
                        } else {
                            // Insertar en room
                            repo.insertUser(userDomainEntity)
                        }

                        val intent = if (staff.isSupplier == "1") {
                            Intent(context, FabricanteActivity::class.java)
                        } else {
                            Intent(context, HomeActivity::class.java)
                        }
                        updateTokenFire()
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        editor.putLong("iduser", 0L)
                        editor.putString("token", "")
                        editor.apply()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        }
    }

    private fun updateTokenFire() {
        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("fragmt", "No se pudo obtener token firebase ${it.exception}")
                } else {
                    val tokenf = it.result
                    viewModelIniciar.updateTokenFirebase(tokenf)
                }
            }
    }


    private fun insertarUserLegacy(user: Staff, token: String) {
        val account = Account()
        account.id = 0
        account.dni = user.staffId
        account.email = ""
        account.telefono = user.phone
        account.direccion = user.address
        account.password = ""
        account.token = token
        account.type = if (user.type == "t1") 1 else 2
        account.company_id = user.companyId
        account.company_name = user.nameCompany
        account.company_phone = user.phoneCompany
        account.company_address = user.addressCompany
        account.company_latitude = user.latitude.toString()
        account.company_longitude = user.longitude.toString()
        account.nombre = user.name
        account.company_ruc = user.ruc
        account.url_facturacion = ""
        account.isSupplier = Boolean.parseBoolean(user.isSupplier)
        account.cloudId = user.userId.toLong()
        if (db.insertData(account)) {
            Log.i("fragment", "usuario legacy insertado correctamente: $user")
        } else {
            Log.i("fragment", "usuario legacy hubo un error ")
        }
    }
}