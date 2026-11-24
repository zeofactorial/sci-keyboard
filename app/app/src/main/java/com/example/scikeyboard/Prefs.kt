package com.example.scikeyboard

import android.content.Context
import android.content.SharedPreferences
import java.io.File

/** অ্যাপ ও কীবোর্ডের সব সেটিং (থিম, ফন্ট, ভাষা, ভয়েস ভাষা, উচ্চতা, সাউন্ড, অটোকারেক্ট ইত্যাদি)। */
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

    /** কীবোর্ডের উচ্চতা (শতাংশ): 80 থেকে 180, ডিফল্ট 115 (বড় কী, টাইপ করা সহজ) */
    var heightPct: Int
        get() = sp.getInt("height_pct", 115).coerceIn(80, 180)
        set(v) { sp.edit().putInt("height_pct", v.coerceIn(80, 180)).apply() }

    /** ভলিউম বাটন দিয়ে কার্সর সরানো: Vol ▲ = বাঁয়ে, Vol ▼ = ডানে */
    var volCursor: Boolean
        get() = sp.getBoolean("vol_cursor", true)
        set(v) { sp.edit().putBoolean("vol_cursor", v).apply() }

    /** কী চাপলে ক্লিক সাউন্ড */
    var sound: Boolean
        get() = sp.getBoolean("sound", false)
        set(v) { sp.edit().putBoolean("sound", v).apply() }

    /** কী চাপলে ভাইব্রেশন */
    var haptic: Boolean
        get() = sp.getBoolean("haptic", true)
        set(v) { sp.edit().putBoolean("haptic", v).apply() }

    /** অটোকারেক্ট */
    var autocorrect: Boolean
        get() = sp.getBoolean("autocorrect", false)
        set(v) { sp.edit().putBoolean("autocorrect", v).apply() }

    /** সোয়াইপ টাইপিং */
    var swipe: Boolean
        get() = sp.getBoolean("swipe", true)
        set(v) { sp.edit().putBoolean("swipe", v).apply() }

    /** সাম্প্রতিক ইমোজি (নতুনটা আগে), প্রতিটি আলাদা লাইনে */
    var recentEmoji: List<String>
        get() = (sp.getString("recent_emoji", "") ?: "").split('\n').filter { it.isNotEmpty() }
        set(v) { sp.edit().putString("recent_emoji", v.joinToString("\n")).apply() }

    fun addRecentEmoji(e: String) {
        val list = ArrayList(recentEmoji)
        list.remove(e)
        list.add(0, e)
        while (list.size > 32) list.removeAt(list.size - 1)
        recentEmoji = list
    }

    /** শর্টকাট: প্রতি লাইনে  trigger=পুরো লেখা  (যেমন  /mail=name@example.com ) */
    var shortcutsRaw: String
        get() = sp.getString("shortcuts", "") ?: ""
        set(v) { sp.edit().putString("shortcuts", v).apply() }

    fun shortcuts(): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        for (line in shortcutsRaw.split('\n')) {
            val i = line.indexOf('=')
            if (i <= 0) continue
            val k = line.substring(0, i).trim()
            val v = line.substring(i + 1).trim().replace("\\n", "\n")
            if (k.isNotEmpty() && v.isNotEmpty() && !k.any { it.isWhitespace() }) map[k] = v
        }
        return map
    }

    /** ছবি বদলালে বাড়ে, যাতে কীবোর্ড সাথে সাথে নতুন ছবি নেয়। */
    var bgRev: Long
        get() = sp.getLong("bg_rev", 0L)
        set(v) { sp.edit().putLong("bg_rev", v).apply() }

    /** সেটিং বদলালে কীবোর্ডকে সাথে সাথে জানানোর জন্য। */
    fun listen(l: SharedPreferences.OnSharedPreferenceChangeListener) {
        sp.registerOnSharedPreferenceChangeListener(l)
    }

    fun unlisten(l: SharedPreferences.OnSharedPreferenceChangeListener) {
        sp.unregisterOnSharedPreferenceChangeListener(l)
    }

    val bgFile: File get() = File(app.filesDir, "keyboard_bg.jpg")

    fun hasBg(): Boolean = bgFile.exists()

    /** নিজের স্টিকার/GIF যেখানে জমা থাকে */
    val stickerDir: File
        get() = File(app.filesDir, "stickers").also { if (!it.exists()) it.mkdirs() }

    fun stickerFiles(): List<File> =
        (stickerDir.listFiles()?.toList() ?: emptyList())
            .filter { it.isFile }
            .sortedByDescending { it.lastModified() }
}
