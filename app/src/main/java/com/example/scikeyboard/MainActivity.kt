package com.example.scikeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.io.FileOutputStream

/** সেটআপ ও সেটিং স্ক্রিন: কার্ড-ভিত্তিক সাজানো পেজ (লাইট/ডার্ক দুটোতেই)। */
class MainActivity : Activity() {

    private lateinit var prefs: Prefs
    private val reqPickPhoto = 101
    private var scroll: ScrollView? = null
    private var statusView: TextView? = null

    private var cBg = 0
    private var cCard = 0
    private var cText = 0
    private var cSub = 0
    private var cLine = 0
    private var cChip = 0
    private val cAccent = Color.parseColor("#3D7BFF")
    private val cAccentDark = Color.parseColor("#2F63D6")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        val dark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        if (dark) {
            cBg = Color.parseColor("#121316")
            cCard = Color.parseColor("#1E2024")
            cText = Color.parseColor("#F2F3F5")
            cSub = Color.parseColor("#9AA0AA")
            cLine = Color.parseColor("#33363D")
            cChip = Color.parseColor("#2A2D33")
        } else {
            cBg = Color.parseColor("#F3F4F8")
            cCard = Color.parseColor("#FFFFFF")
            cText = Color.parseColor("#1B1D22")
            cSub = Color.parseColor("#6B7280")
            cLine = Color.parseColor("#E5E7EB")
            cChip = Color.parseColor("#EEF1F7")
        }
        build()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    /** অ্যাপের ভাষা অনুযায়ী লেখা বেছে নেয়। */
    private fun tr(en: String, bn: String): String = if (prefs.lang == "bn") bn else en

    // ---------------------------------------------------------------- page

    private fun build() {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(36), dp(16), dp(32))
        }

        col.addView(label("Scientific Keyboard", 28f, cText, bold = true))
        col.addView(
            label(
                tr(
                    "A–Z keyboard with all the math symbols from MS Word's equation editor.",
                    "a–z কীবোর্ড + MS Word equation-এর মতো গাণিতিক চিহ্ন।"
                ),
                14f, cSub
            )
        )
        col.addView(space(12))

        val st = label("", 14f, cText, bold = true)
        st.background = round(cChip, 12)
        st.setPadding(dp(14), dp(10), dp(14), dp(10))
        statusView = st
        col.addView(st)
        updateStatus()
        col.addView(space(14))

        // ১) সেটআপ
        card(col, "🚀", tr("Set up", "সেটআপ")) {
            addView(action(tr("1. Enable the keyboard", "১. কীবোর্ড চালু করুন"), primary = true) { openInputSettings() })
            addView(action(tr("2. Choose the keyboard", "২. কীবোর্ড বেছে নিন")) { openPicker() })
            addView(space(6))
            addView(label(tr("3. Type here to test", "৩. এখানে টাইপ করে টেস্ট করুন"), 13f, cSub, bold = true))
            addView(testField())
            addView(space(4))
        }

        // ২) ভাষা
        card(col, "🌐", tr("App language", "অ্যাপের ভাষা")) {
            addRows(
                listOf(
                    choice("English", prefs.lang == "en") { prefs.lang = "en"; rebuild() },
                    choice("বাংলা", prefs.lang == "bn") { prefs.lang = "bn"; rebuild() }
                )
            )
        }

        // ৩) থিম ও ছবি
        card(col, "🎨", tr("Theme", "থিম")) {
            addRows(
                Themes.all.map { t ->
                    choice(t.name, prefs.themeId == t.id) {
                        prefs.themeId = t.id
                        if (t.image && !prefs.hasBg()) {
                            toast(tr("Now choose a photo below", "এখন নিচ থেকে একটা ছবি বেছে নিন"))
                        }
                        rebuild()
                    }
                }
            )
            addView(action(tr("Choose a photo from gallery", "গ্যালারি থেকে ছবি বেছে নিন")) { pickPhoto() })
            if (prefs.hasBg()) {
                addView(action(tr("Remove the photo", "ছবিটা সরিয়ে ফেলুন")) {
                    prefs.bgFile.delete()
                    prefs.bgRev = System.currentTimeMillis()
                    if (prefs.themeId == "photo") prefs.themeId = "dark"
                    rebuild()
                })
            }
        }

        // ৪) ফন্ট
        card(col, "🔤", tr("Key font", "কী-এর ফন্ট")) {
            addRows(
                Fonts.all.map { f ->
                    choice(f.name, prefs.fontId == f.id) {
                        prefs.fontId = f.id
                        rebuild()
                    }
                }
            )
        }

        // ৫) কীবোর্ডের আচরণ
        card(col, "⌨️", tr("Keyboard", "কীবোর্ড")) {
            addView(
                stepper(
                    tr("Key size", "কী-এর আকার"),
                    "${prefs.heightPct}%",
                    { prefs.heightPct = prefs.heightPct - 10; rebuild() },
                    { prefs.heightPct = prefs.heightPct + 10; rebuild() }
                )
            )
            addView(divider())
            addView(toggle(tr("Key sound", "কী সাউন্ড"), null, prefs.sound) { prefs.sound = it })
            addView(divider())
            addView(toggle(tr("Vibration", "ভাইব্রেশন"), null, prefs.haptic) { prefs.haptic = it })
            addView(divider())
            addView(
                toggle(
                    tr("Autocorrect", "অটোকারেক্ট"),
                    tr("Fixes small typos when you press space", "স্পেস চাপলে ছোট বানান ভুল ঠিক করে"),
                    prefs.autocorrect
                ) { prefs.autocorrect = it }
            )
            addView(divider())
            addView(
                toggle(
                    tr("Swipe typing", "সোয়াইপ টাইপিং"),
                    tr("Slide over letters to type a word", "অক্ষরের ওপর দিয়ে আঙুল টেনে শব্দ লিখুন"),
                    prefs.swipe
                ) { prefs.swipe = it }
            )
            addView(divider())
            addView(
                toggle(
                    tr("Volume keys move cursor", "ভলিউম বাটনে কার্সর সরানো"),
                    tr("Vol ▲ = left, Vol ▼ = right", "Vol ▲ = বাঁয়ে, Vol ▼ = ডানে"),
                    prefs.volCursor
                ) { prefs.volCursor = it }
            )
            addView(action(tr("Shortcuts & stickers", "শর্টকাট ও স্টিকার")) { openExtras() })
        }

        // ৬) ভয়েস
        card(col, "🎤", tr("Voice typing", "ভয়েস টাইপিং")) {
            addRows(
                listOf(
                    choice(tr("Voice language: English", "ভয়েসের ভাষা: English"), prefs.voiceLang == "en-US") {
                        prefs.voiceLang = "en-US"; rebuild()
                    },
                    choice(tr("Voice language: Bangla (বাংলা)", "ভয়েসের ভাষা: বাংলা"), prefs.voiceLang == "bn-BD") {
                        prefs.voiceLang = "bn-BD"; rebuild()
                    }
                )
            )
            addView(action(tr("Allow microphone", "মাইক্রোফোনের অনুমতি দিন")) { openMic() })
        }

        // ৭) শেখা শব্দ
        card(col, "💡", tr("Word suggestions", "শব্দের সাজেশন")) {
            addView(action(tr("Clear learned words", "শেখা শব্দ মুছে ফেলুন")) {
                clearWords()
            })
        }

        // ৮) ব্যবহারবিধি
        card(col, "📖", tr("How to use", "কীভাবে ব্যবহার করবেন")) {
            addView(
                label(
                    tr(
                        "• Tap ⋯ in the top bar to open the tools: big 0-9 digits, emoji, clipboard, text editing, number pad, ∑π symbols, x² / x₂, themes\n" +
                            "• ∑π opens all the math symbols (Greek, Relations, Calculus, Arrows, Accents…)\n" +
                            "• x² / x₂: the next letters or digits you type become superscript / subscript\n" +
                            "• Long-press a letter key to type the small number or symbol shown on it\n" +
                            "• Long-press v to paste the last copied text\n" +
                            "• Volume ▲ / ▼ moves the cursor left / right while the keyboard is open\n" +
                            "• The three words at the top are suggestions; tap one to insert it\n" +
                            "• 🎤 starts voice typing and keeps listening until you tap it again; long-press 🎤 to switch English / Bangla; ⌨ switches to another keyboard\n" +
                            "• Tap ⇧ twice for Caps Lock; hold ⌫ to delete continuously",
                        "• উপরের ⋯ চাপলে টুল খোলে: বড় 0-9 সংখ্যা, ইমোজি, ক্লিপবোর্ড, টেক্সট এডিট, নাম্বার প্যাড, ∑π চিহ্ন, x² / x₂, থিম\n" +
                            "• ∑π চাপলে সব গাণিতিক চিহ্নের পেজ খোলে (Greek, Relations, Calculus, Arrows, Accents…)\n" +
                            "• x² / x₂ চাপলে পরের টাইপ করা অক্ষর/সংখ্যা সুপারস্ক্রিপ্ট/সাবস্ক্রিপ্ট হবে\n" +
                            "• অক্ষরের কী চেপে ধরলে তার ওপরের ছোট সংখ্যা/চিহ্ন টাইপ হবে\n" +
                            "• v চেপে ধরলে সবশেষ কপি করা লেখা পেস্ট হবে\n" +
                            "• কীবোর্ড খোলা থাকলে ভলিউম ▲ / ▼ কার্সর বাঁয়ে / ডানে সরায়\n" +
                            "• উপরের তিনটা শব্দ সাজেশন, চাপলেই শব্দ বসে যায়\n" +
                            "• 🎤 ভয়েস টাইপিং শুরু করে, আবার না চাপা পর্যন্ত চলতে থাকে; 🎤 চেপে ধরলে English ⇄ বাংলা; ⌨ অন্য কীবোর্ডে বদলায়\n" +
                            "• ⇧ দুইবার চাপলে Caps Lock; ⌫ চেপে ধরলে ক্রমাগত মুছবে"
                    ),
                    13f, cSub
                ).apply { setLineSpacing(0f, 1.25f) }
            )
            addView(space(4))
        }

        val sv = ScrollView(this).apply {
            setBackgroundColor(cBg)
            isVerticalScrollBarEnabled = false
            addView(col)
        }
        scroll = sv
        setContentView(sv)
    }

    /** সেটিং বদলের পর পেজ নতুন করে সাজায়, কিন্তু স্ক্রল যেখানে ছিল সেখানেই থাকে। */
    private fun rebuild() {
        val y = scroll?.scrollY ?: 0
        build()
        scroll?.post { scroll?.scrollTo(0, y) }
    }

    private fun updateStatus() {
        val tv = statusView ?: return
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val enabled = imm.enabledInputMethodList.any { it.packageName == packageName }
        val cur = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: ""
        val active = cur.startsWith("$packageName/")
        val (msg, color) = when {
            active -> Pair(
                tr("● Keyboard is on and selected", "● কীবোর্ড চালু ও বেছে নেওয়া আছে"),
                Color.parseColor("#1E9E5A")
            )
            enabled -> Pair(
                tr("● Enabled. Now choose it (step 2)", "● চালু আছে। এখন বেছে নিন (ধাপ ২)"),
                Color.parseColor("#D98A00")
            )
            else -> Pair(
                tr("● Not enabled yet (step 1)", "● এখনও চালু হয়নি (ধাপ ১)"),
                Color.parseColor("#D64545")
            )
        }
        tv.text = msg
        tv.setTextColor(color)
    }

    // ---------------------------------------------------------------- actions

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun openInputSettings() {
        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

    private fun openPicker() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
    }

    private fun openExtras() {
        startActivity(Intent(this, ExtrasActivity::class.java))
    }

    private fun openMic() {
        startActivity(Intent(this, MicPermissionActivity::class.java))
    }

    private fun clearWords() {
        WordStore(this).clear()
        Toast.makeText(this, tr("Learned words cleared", "শেখা শব্দ মুছে ফেলা হয়েছে"), Toast.LENGTH_SHORT).show()
    }

    @Suppress("DEPRECATION")
    private fun pickPhoto() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        startActivityForResult(Intent.createChooser(i, "Photo"), reqPickPhoto)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqPickPhoto && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            if (saveBackground(uri)) {
                prefs.bgRev = System.currentTimeMillis()
                prefs.themeId = "photo"
                toast(tr("Photo saved. Theme set to My photo.", "ছবি সেভ হয়েছে। থিম \"My photo\" করা হয়েছে।"))
            } else {
                toast(tr("Could not read that photo", "ছবিটা পড়া যায়নি"))
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

    // ---------------------------------------------------------------- UI building blocks

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun round(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
        }

    private fun pressBg(normal: Int, pressed: Int, radiusDp: Int): StateListDrawable =
        StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), round(pressed, radiusDp))
            addState(intArrayOf(), round(normal, radiusDp))
        }

    private fun space(heightDp: Int): View =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(heightDp))
        }

    private fun divider(): View =
        View(this).apply {
            setBackgroundColor(cLine)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
        }

    private fun label(text: String, sp: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = text
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    private fun card(parent: LinearLayout, icon: String, title: String, body: LinearLayout.() -> Unit) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = round(cCard, 16)
            setPadding(dp(16), dp(14), dp(16), dp(10))
        }
        box.addView(label("$icon  $title", 16f, cText, bold = true))
        box.addView(space(6))
        box.body()
        parent.addView(
            box,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
        )
    }

    private fun LinearLayout.addRows(rows: List<View>) {
        rows.forEachIndexed { i, v ->
            if (i > 0) addView(divider())
            addView(v)
        }
    }

    /** একটা বেছে নেওয়ার সারি; বাছা থাকলে ডানে ✓। */
    private fun choice(name: String, selected: Boolean, onClick: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(48)
            setPadding(dp(6), 0, dp(6), 0)
            isClickable = true
            background = pressBg(0, cChip, 10)
            setOnClickListener { onClick() }
        }
        row.addView(
            TextView(this).apply {
                text = name
                setTextColor(if (selected) cAccent else cText)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                if (selected) setTypeface(typeface, Typeface.BOLD)
            },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        if (selected) {
            row.addView(
                TextView(this).apply {
                    text = "✓"
                    setTextColor(cAccent)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    setTypeface(typeface, Typeface.BOLD)
                }
            )
        }
        return row
    }

    private fun action(name: String, primary: Boolean = false, onClick: () -> Unit): View =
        TextView(this).apply {
            text = name
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(if (primary) Color.WHITE else cAccent)
            background = if (primary) pressBg(cAccent, cAccentDark, 12) else pressBg(cChip, cLine, 12)
            minHeight = dp(48)
            isClickable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(2)
            }
        }

    private fun toggle(name: String, sub: String?, checked: Boolean, onChange: (Boolean) -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(52)
            setPadding(dp(6), dp(6), dp(6), dp(6))
        }
        val texts = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        texts.addView(label(name, 15f, cText))
        if (sub != null) texts.addView(label(sub, 12f, cSub))
        row.addView(texts, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        val sw = Switch(this)
        sw.isChecked = checked
        sw.setOnCheckedChangeListener { _, isOn -> onChange(isOn) }
        row.addView(sw)
        return row
    }

    private fun roundBtn(sym: String, onClick: () -> Unit): View =
        TextView(this).apply {
            text = sym
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setTextColor(cAccent)
            background = pressBg(cChip, cLine, 20)
            isClickable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
        }

    private fun stepper(name: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(52)
            setPadding(dp(6), dp(6), dp(6), dp(6))
        }
        row.addView(
            label(name, 15f, cText),
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        row.addView(roundBtn("−", onMinus))
        row.addView(
            TextView(this).apply {
                text = value
                gravity = Gravity.CENTER
                minWidth = dp(64)
                setTextColor(cText)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTypeface(typeface, Typeface.BOLD)
            }
        )
        row.addView(roundBtn("+", onPlus))
        return row
    }

    private fun testField(): View =
        EditText(this).apply {
            hint = tr(
                "Type here (open the 🎤 / ∑π tools to try them)",
                "এখানে টাইপ করুন (🎤 / ∑π টুল চেষ্টা করুন)"
            )
            setHintTextColor(cSub)
            setTextColor(cText)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            minLines = 3
            gravity = Gravity.TOP
            background = round(cChip, 12)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(6) }
        }
}
