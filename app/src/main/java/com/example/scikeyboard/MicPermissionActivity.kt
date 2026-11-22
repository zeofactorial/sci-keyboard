package com.example.scikeyboard

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle

/** কীবোর্ড থেকে সরাসরি পারমিশন চাওয়া যায় না, তাই এই ছোট স্বচ্ছ স্ক্রিন মাইক্রোফোনের অনুমতি চায়। */
class MicPermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            finish()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        finish()
    }
}
