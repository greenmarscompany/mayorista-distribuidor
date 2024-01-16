package com.greenmars.distribuidor.ui.product

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.ActivityAgregarProductoBinding
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.DetailMeasurement
import com.greenmars.distribuidor.domain.Marke
import com.greenmars.distribuidor.domain.UnitMeasurement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

@AndroidEntryPoint
class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private val agregarProductoViewModel by viewModels<AgregarProductoViewModel>()
    private lateinit var fileImage: File
    private lateinit var uriImage: Uri

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            cargarImagen(uri)
            uriImage = uri
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        agregarProductoViewModel.getCategories()
        agregarProductoViewModel.getMarkes()
        agregarProductoViewModel.getDetailMeasurements()
        agregarProductoViewModel.getUnitMeasurements()

        binding.btnSelecionar.setOnClickListener {
            selecionarFoto(pickMedia)
        }

        binding.btnLimpiarFoto.setOnClickListener {
            binding.ivProduct.setImageDrawable(null)
        }

        binding.btnLimpiar.setOnClickListener {
            binding.txtDescription.text = null
            binding.txtMedida.editText?.text = null
            binding.txtPrecioU.editText?.text = null
            binding.ivProduct.setImageDrawable(null)
        }
        initUI()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initUI() {
        mostrarDescuentosPanel()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        initState()
        guardarProducto()
    }

    private fun upload(): File {
        val fileDir = applicationContext.filesDir
        val file = File(fileDir, "${UUID.randomUUID()}.png")
        val inputStream = contentResolver.openInputStream(uriImage)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        inputStream.close()

        return file
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun selecionarFoto(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun cargarImagen(uri: Uri) {
        Glide.with(this)
                .load(uri)
                .into(binding.ivProduct)
    }

    private fun guardarProducto() {
        var categoryPosition = 0
        var marcasPosition = 0
        var detailsPosition = 0
        var unitsPosition = 0
        (binding.dmCategories.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            categoryPosition = position
        }
        (binding.dmMarcas.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            marcasPosition = position
        }
        (binding.dmDetails.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            detailsPosition = position
        }
        (binding.dmUnits.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            unitsPosition = position
        }

        binding.btnGuardar.setOnClickListener {
            val description = binding.txtDescription.text.toString()
            val medida = binding.txtMedida.editText?.text.toString().toInt()
            val precio = binding.txtPrecioU.editText?.text.toString().toDouble()
            Log.i("AgregarActi", "Data para guardar: ${categoryPosition}, ${marcasPosition}, ${detailsPosition}, ${unitsPosition}, ${description}, ${medida}, ${precio}")
            agregarProductoViewModel.saveProduct(categoryPosition, marcasPosition, detailsPosition, unitsPosition, description, medida, precio)
        }
    }

    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    agregarProductoViewModel.categories.collect {
                        Log.i("AgregarProducto", "Categories: $it")
                        adapterCategories(it)
                    }
                }

                launch {
                    agregarProductoViewModel.markes.collect {
                        adapterMakers(it)
                    }
                }

                launch {
                    agregarProductoViewModel.details.collect {
                        adapterDetails(it)
                    }
                }

                launch {
                    agregarProductoViewModel.units.collect {
                        adapterUnits(it)
                    }
                }

                launch {
                    agregarProductoViewModel.product.collect {
                        if (it.status == 200) {
                            Toast.makeText(baseContext, it.message, Toast.LENGTH_LONG).show()
                            Log.i("agr", "Producto registrado: ${it.data}")
                            saveImage(it.data.idproduct)
                            finish()
                        }
                    }
                }

                launch {
                    agregarProductoViewModel.image.collect {
                        Log.i("agregarp", "Imagen: $it")
                    }
                }

            }
        }
    }

    private fun saveImage(idproduct: Int) {
        agregarProductoViewModel.saveImage(upload(), idproduct.toString())
    }

    private fun adapterUnits(unitMeasurements: List<UnitMeasurement>) {
        val adapter = ArrayAdapter(baseContext, R.layout.list_item_units, unitMeasurements.map { it.name })
        (binding.dmUnits.editText as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(adapter)
        }
    }

    private fun adapterDetails(detailMeasurements: List<DetailMeasurement>) {
        val adapter = ArrayAdapter(baseContext, R.layout.list_item_details, detailMeasurements.map { it.name })
        (binding.dmDetails.editText as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(adapter)
        }
    }

    private fun adapterMakers(markes: List<Marke>) {

        val adapter = ArrayAdapter(baseContext, R.layout.list_item_make, markes.map { it.name })
        (binding.dmMarcas.editText as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(adapter)
        }
    }

    private fun mostrarDescuentosPanel() {
        binding.cvDescuentos.visibility = View.GONE
        binding.chkShowDescuentos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cvDescuentos.visibility = View.VISIBLE
            } else {
                binding.cvDescuentos.visibility = View.GONE
            }
        }
    }

    private fun adapterCategories(items: List<Category>) {
        val adapter = ArrayAdapter(baseContext, R.layout.list_item_category, items.map { it.name })
        (binding.dmCategories.editText as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(adapter)
        }
    }

}