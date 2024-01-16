package com.greenmars.distribuidor.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment

class ImagePicker(private val context: Context) {

    private var onImagePickedListener: ((Uri) -> Unit)? = null

    fun setOnImagePickedListener(listener: (Uri) -> Unit) {
        onImagePickedListener = listener
    }

    fun launchPicker() {
        if (context is Activity || context is Fragment) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            when (context) {
                is Activity -> {
                    context.startActivityForResult(intent, PICK_IMAGE_REQUEST)
                }
                is Fragment -> {
                    context.startActivityForResult(intent, PICK_IMAGE_REQUEST)
                }
            }
        } else {
            throw IllegalArgumentException("Context must be an instance of Activity or Fragment.")
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                onImagePickedListener?.invoke(it)
            }
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}