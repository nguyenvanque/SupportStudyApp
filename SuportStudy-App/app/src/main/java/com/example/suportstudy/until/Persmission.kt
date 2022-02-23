package com.example.suportstudy.until

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object Persmission {
    val STORAGE_REQUEST_CODE = 100
    val IMAGE_PICK_GALLERY_CODE =200
    var storagePermission: Array<String>? =arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

     fun checkStoragePermission(context: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
     fun requestStoragetPermission(context: Activity) {
        context.requestPermissions(
            storagePermission!!,
            STORAGE_REQUEST_CODE
        )
    }

     fun pickFromGallery(context: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        context.startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }
}