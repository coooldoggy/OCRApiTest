package com.coooldoggy.ocrtest.utils

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


val perms: Array<String> = arrayOf(
    CAMERA,
    WRITE_EXTERNAL_STORAGE,
    READ_EXTERNAL_STORAGE
)

val PERM_REQUEST_CODE_GOOGLE: Int = 1000
val PERM_REQUEST_CODE_KAKAO: Int = 1001
val PERM_REQUEST_CODE_GCP: Int = 1002

fun Context.getCheckPermission(perms: Array<String>): Boolean{
    perms.forEach {
        val result1:Int = ContextCompat.checkSelfPermission(this, it)
        if (result1 != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}


fun Context.requestPermissions(activity: Activity, perms: Array<String>, permCode: Int){
    ActivityCompat.requestPermissions(activity, perms, permCode)
}