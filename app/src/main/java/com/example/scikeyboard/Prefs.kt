package com.example.scikeyboard

import android.content.Context
import android.content.SharedPreferences
import java.io.File

/** অ্যাপ ও কীবোর্ডের সব সেটিং (থিম, ফন্ট, ভাষা, ভয়েস ভাষা, ক্লিপবোর্ড)। */
class Prefs(context: Context) {

    private val app = context.applicationContext
    private val sp: SharedPreferences = app.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var themeId: String
        get() = sp.getString("theme", "dark") ?: "dark"
        set(v) { sp.edit().putString("theme", v).apply() }

    var fontId: String
        get() = sp.getString("font", "default") ?: "default"
        set(v) { sp.edit().putString("font", v).apply() }

    /** অ্যাপের ভাষা: "en" বা "bn" */
    var lang: String
        get() = sp.getString("lang", "en") ?: "en"
        set(v) { sp.edit().putString("lang", v).apply() }

    /** ভয়েস টাইপিংয়ের ভাষা: "en-US" বা "bn-BD" */
    var voiceLang: String
        get() = sp.getString("voice_lang", "en-US") ?: "en-US"
        set(v) { sp.edit().putString("voice_lang", v).apply() }

    val bgFile: File get() = File(app.filesDir, "keyboard_bg.jpg")

    fun hasBg(): Boolean = bgFile.exists()
}
