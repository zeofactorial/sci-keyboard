package com.example.scikeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.FileOutputStream

/** সেটআপ ও সেটিং স্ক্রিন: কীবোর্ড চালু, ভাষা, থিম, ফন্ট, ভয়েস, ছবির ব্যাকগ্রাউন্ড। */
class MainActivity : Activity() {

    private lateinit var prefs: Prefs
    private val reqPickPhoto = 101
    private var scroll: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        build()
    }

    /** অ্যাপের ভাষা অনুযায়ী লেখা বেছে নেয়। */
    private fun tr(en: String, bn: String): String = if (prefs.lang == "bn") bn else en

    private fun build() {
        val pad = dp(20)
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }

        fun text(s: String, sp: Float, bold: Boolean = false, bottom: Int = 12) {
            col.addView(TextView(this).apply {
                text = s
                setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
                if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, dp(bottom))
            })
        }

        fun button(label: String, selected: Boolean = false, onClick: () -> Unit) {
            col.addView(
                Button(this).apply {
                    text = if (selected) "✓  $label" else label
                    isAllCaps = false
                    setOnClickListener { onClick() }
                },
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(8) }
            )
        }

        text("Scientific Keyboard", 26f, bold = true)
        text(
            tr(
                "A–Z keyboard with all the math symbols from MS Word's equation editor.",
                "a–z কীবোর্ড + MS Word equation-এর মতো গাণিতিক চিহ্ন।"
            ),
            15f, bottom = 20
        )

        // ভাষা
        text(tr("App language", "অ্যাপের ভাষা"), 16f, bold = true, bottom = 4)
        button("English", prefs.lang == "en") { prefs.lang = "en"; rebuild() }
        button("বাংলা", prefs.lang == "bn") { prefs.lang = "bn"; rebuild() }

        // সেটআপ
        text(tr("Step 1", "ধাপ ১"), 16f, bold = true, bottom = 4)
        button(tr("Enable the keyboard (opens Settings)", "কীবোর্ড চালু করুন (Settings খুলবে)")) {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        text(tr("Step 2", "ধাপ ২"), 16f, bold = true, bottom = 4)
        button(tr("Choose the keyboard", "কীবোর্ড বেছে নিন")) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showInputMethodPicker()
        }

        text(tr("Step 3: type here to test", "ধাপ ৩: এখানে টাইপ করে টেস্ট করুন"), 16f, bold = true, bottom = 4)
        col.addView(
            EditText(this).apply {
                hint = tr(
                    "Type here (open the 🎤 / ∑π tools to try them)",
                    "এখানে টাইপ করুন (🎤 / ∑π টুল চেষ্টা করুন)"
                )
                minLines = 3
                gravity = Gravity.TOP
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(20) }
        )

        // থিম
        text(tr("Theme", "থিম"), 16f, bold = true, bottom = 4)
        Themes.all.forEach { t ->
            button(t.name, prefs.themeId == t.id) {
                prefs.themeId = t.id
                if (t.image && !prefs.hasBg()) {
                    Toast.makeText(
                        this,
                        tr("Now choose a photo below", "এখন নিচ থেকে একটা ছবি বেছে নিন"),
                        Toast.LENGTH_LONG
                    ).show()
                }
                rebuild()
            }
        }

        // ছবির ব্যাকগ্রাউন্ড
        text(tr("Photo background", "ছবির ব্যাকগ্রাউন্ড"), 16f, bold = true, bottom = 4)
        button(tr("Choose a photo from gallery", "গ্যালারি থেকে ছবি বেছে নিন")) {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            @Suppress("DEPRECATION")
            startActivityForResult(Intent.createChooser(i, "Photo"), reqPickPhoto)
        }
        if (prefs.hasBg()) {
            button(tr("Remove the photo", "ছবিটা সরিয়ে ফেলুন")) {
                prefs.bgFile.delete()
                prefs.bgRev = System.currentTimeMillis()
                if (prefs.themeId == "photo") prefs.themeId = "dark"
                rebuild()
            }
        }

        // ফন্ট
        text(tr("Key font", "কী-এর ফন্ট"), 16f, bold = true, bottom = 4)
        Fonts.all.forEach { f ->
            button(f.name, prefs.fontId == f.id) {
                prefs.fontId = f.id
                rebuild()
            }
        }

        // কীবোর্ডের আচরণ
        text(tr("Keyboard", "কীবোর্ড"), 16f, bold = true, bottom = 4)
        button(tr("Taller keys (now ${prefs.heightPct}%)", "কী বড় করুন (এখন ${prefs.heightPct}%)")) {
            prefs.heightPct = prefs.heightPct + 10; rebuild()
        }
        button(tr("Shorter keys (now ${prefs.heightPct}%)", "কী ছোট করুন (এখন ${prefs.heightPct}%)")) {
            prefs.heightPct = prefs.heightPct - 10; rebuild()
        }
        button(tr("Key sound", "কী সাউন্ড"), prefs.sound) { prefs.sound = !prefs.sound; rebuild() }
        button(tr("Vibration", "ভাইব্রেশন"), prefs.haptic) { prefs.haptic = !prefs.haptic; rebuild() }
        button(tr("Autocorrect", "অটোকারেক্ট"), prefs.autocorrect) { prefs.autocorrect = !prefs.autocorrect; rebuild() }
        button(tr("Swipe typing", "সোয়াইপ টাইপিং"), prefs.swipe) { prefs.swipe = !prefs.swipe; rebuild() }
        button(tr("Shortcuts & stickers", "শর্টকাট ও স্টিকার")) {
            startActivity(Intent(this, ExtrasActivity::class.java))
        }

        // ভয়েস
        text(tr("Voice typing", "ভয়েস টাইপিং"), 16f, bold = true, bottom = 4)
        button(tr("Voice language: English", "ভয়েসের ভাষা: English"), prefs.voiceLang == "en-US") {
            prefs.voiceLang = "en-US"; rebuild()
        }
        button(tr("Voice language: Bangla (বাংলা)", "ভয়েসের ভাষা: বাংলা"), prefs.voiceLang == "bn-BD") {
            prefs.voiceLang = "bn-BD"; rebuild()
        }
        button(tr("Allow microphone", "মাইক্রোফোনের অনুমতি দিন")) {
            startActivity(Intent(this, MicPermissionActivity::class.java))
        }

        // শেখা শব্দ
        text(tr("Word suggestions", "শব্দের সাজেশন"), 16f, bold = true, bottom = 4)
        button(tr("Clear learned words", "শেখা শব্দ মুছে ফেলুন")) {
            WordStore(this).clear()
            Toast.makeText(
                this,
                tr("Learned words cleared", "শেখা শব্দ মুছে ফেলা হয়েছে"),
                Toast.LENGTH_SHORT
            ).show()
        }

        // ব্যবহারবিধি
        text(tr("How to use", "কীভাবে ব্যবহার করবেন"), 16f, bold = true, bottom = 4)
        text(
            tr(
                "• Tap ⋯ in the top bar to open the tools: big 0-9 digits, emoji, clipboard, text editing, number pad, ∑π symbols, x² / x₂, themes\n" +
                    "• ∑π opens all the math symbols (Greek, Relations, Calculus, Arrows, Accents…)\n" +
                    "• x² / x₂: the next letters or digits you type become superscript / subscript\n" +
                    "• Long-press a letter key to type the small number or symbol shown on it\n" +
                    "• The three words at the top are suggestions; tap one to insert it\n" +
                    "• 🎤 starts voice typing and keeps listening until you tap it again; long-press 🎤 to switch English / Bangla; ⌨ switches to another keyboard\n" +
                    "• Tap ⇧ twice for Caps Lock; hold ⌫ to delete continuously",
                "• উপরের ⋯ চাপলে টুল খোলে: বড় 0-9 সংখ্যা, ইমোজি, ক্লিপবোর্ড, টেক্সট এডিট, নাম্বার প্যাড, ∑π চিহ্ন, x² / x₂, থিম\n" +
                    "• ∑π চাপলে সব গাণিতিক চিহ্নের পেজ খোলে (Greek, Relations, Calculus, Arrows, Accents…)\n" +
                    "• x² / x₂ চাপলে পরের টাইপ করা অক্ষর/সংখ্যা সুপারস্ক্রিপ্ট/সাবস্ক্রিপ্ট হবে\n" +
                    "• অক্ষরের কী চেপে ধরলে তার ওপরের ছোট সংখ্যা/চিহ্ন টাইপ হবে\n" +
                    "• উপরের তিনটা শব্দ সাজেশন, চাপলেই শব্দ বসে যায়\n" +
                    "• 🎤 ভয়েস টাইপিং শুরু করে, আবার না চাপা পর্যন্ত চলতে থাকে; 🎤 চেপে ধরলে English ⇄ বাংলা; ⌨ অন্য কীবোর্ডে বদলায়\n" +
                    "• ⇧ দুইবার চাপলে Caps Lock; ⌫ চেপে ধরলে ক্রমাগত মুছবে"
            ),
            14f
        )

        val sv = ScrollView(this).apply { addView(col) }
        scroll = sv
        setContentView(sv)
    }

    /** সেটিং বদলের পর পেজ নতুন করে সাজায়, কিন্তু স্ক্রল যেখানে ছিল সেখানেই থাকে। */
    private fun rebuild() {
        val y = scroll?.scrollY ?: 0
        build()
        scroll?.post { scroll?.scrollTo(0, y) }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqPickPhoto && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            if (saveBackground(uri)) {
                prefs.bgRev = System.currentTimeMillis()
                prefs.themeId = "photo"
                Toast.makeText(
                    this,
                    tr("Photo saved. Theme set to My photo.", "ছবি সেভ হয়েছে। থিম \"My photo\" করা হয়েছে।"),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    tr("Could not read that photo", "ছবিটা পড়া যায়নি"),
                    Toast.LENGTH_LONG
                ).show()
            }
            rebuild()
        }
    }

    /** বড় ছবি ছোট করে (প্রায় ১০০০px) অ্যাপের ভেতরে সেভ করে। */
    private fun saveBackground(uri: Uri): Boolean {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= 1000 && bounds.outHeight / (sample * 2) >= 1000) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp: Bitmap = contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opts)
            } ?: return false
            FileOutputStream(prefs.bgFile).use { bmp.compress(Bitmap.CompressFormat.JPEG, 88, it) }
            true
        } catch (e: Throwable) {
            false
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
