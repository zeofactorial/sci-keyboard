package com.example.scikeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

/**
 * শর্টকাট (text expander) ও নিজের স্টিকার/GIF ম্যানেজ করার স্ক্রিন।
 * কীবোর্ডের ◐ পেজ থেকে খোলে।
 */
class ExtrasActivity : Activity() {

    private lateinit var prefs: Prefs
    private val reqPick = 201
    private lateinit var shortcutsBox: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        build()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun build() {
        val pad = dp(18)
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }

        fun text(s: String, sp: Float, bold: Boolean = false, bottom: Int = 10) {
            col.addView(TextView(this).apply {
                text = s
                setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
                if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, dp(bottom))
            })
        }

        fun button(label: String, onClick: () -> Unit) {
            col.addView(
                Button(this).apply {
                    text = label
                    isAllCaps = false
                    setOnClickListener { onClick() }
                },
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(8) }
            )
        }

        // ---------------------------------------------------------- শর্টকাট
        text("Shortcuts (text expander)", 22f, bold = true)
        text(
            "প্রতি লাইনে একটা শর্টকাট লিখুন:  শর্টকাট=পুরো লেখা\n" +
                "যেমন:  /mail=myname@example.com\n" +
                "যেমন:  /addr=Kushtia, Bangladesh\n" +
                "কীবোর্ডে শর্টকাট লিখে space চাপলে পুরো লেখা বসে যাবে। " +
                "নতুন লাইন চাইলে \\n লিখুন। শর্টকাটে space থাকবে না।",
            14f, bottom = 12
        )
        shortcutsBox = EditText(this).apply {
            setText(prefs.shortcutsRaw)
            hint = "/mail=myname@example.com"
            minLines = 4
            gravity = Gravity.TOP
        }
        col.addView(
            shortcutsBox,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        )
        button("Save shortcuts") {
            prefs.shortcutsRaw = shortcutsBox.text.toString().trim()
            Toast.makeText(
                this,
                "Saved (${prefs.shortcuts().size} shortcuts)",
                Toast.LENGTH_SHORT
            ).show()
        }

        // ---------------------------------------------------------- স্টিকার / GIF
        text("My stickers & GIFs", 22f, bold = true, bottom = 6)
        text(
            "নিজের ছবি বা GIF যোগ করুন। কীবোর্ডের ⋯ → 🖼 থেকে পাঠানো যায়। " +
                "যে অ্যাপ ছবি/GIF নেয় (WhatsApp, Messenger, Telegram ইত্যাদি) শুধু সেখানে কাজ করবে।",
            14f, bottom = 12
        )
        button("Add images / GIFs from gallery") {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            @Suppress("DEPRECATION")
            startActivityForResult(Intent.createChooser(i, "Images"), reqPick)
        }

        val files = prefs.stickerFiles()
        if (files.isEmpty()) {
            text("No stickers added yet.", 14f)
        } else {
            text("${files.size} saved (tap Delete to remove one):", 14f, bottom = 6)
            files.forEach { f ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }
                val img = ImageView(this)
                try {
                    val o = BitmapFactory.Options().apply { inSampleSize = 4 }
                    img.setImageBitmap(BitmapFactory.decodeFile(f.path, o))
                } catch (e: Throwable) {
                    // ছবি না দেখাতে পারলেও সমস্যা নেই
                }
                img.scaleType = ImageView.ScaleType.CENTER_CROP
                row.addView(img, LinearLayout.LayoutParams(dp(56), dp(56)))
                row.addView(
                    TextView(this).apply {
                        text = f.name
                        setPadding(dp(10), 0, dp(10), 0)
                        isSingleLine = true
                    },
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                )
                row.addView(
                    Button(this).apply {
                        text = "Delete"
                        isAllCaps = false
                        setOnClickListener {
                            f.delete()
                            rebuild()
                        }
                    }
                )
                col.addView(
                    row,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = dp(6) }
                )
            }
        }

        setContentView(ScrollView(this).apply { addView(col) })
    }

    private fun rebuild() {
        val keep = if (::shortcutsBox.isInitialized) shortcutsBox.text.toString() else null
        build()
        if (keep != null) shortcutsBox.setText(keep)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != reqPick || resultCode != RESULT_OK || data == null) return
        val uris = ArrayList<Uri>()
        data.data?.let { uris.add(it) }
        val clip = data.clipData
        if (clip != null) {
            for (i in 0 until clip.itemCount) uris.add(clip.getItemAt(i).uri)
        }
        var added = 0
        for (u in uris.distinct()) {
            if (prefs.stickerFiles().size >= 80) break
            if (copySticker(u)) added++
        }
        Toast.makeText(this, "Added $added", Toast.LENGTH_SHORT).show()
        rebuild()
    }

    private fun copySticker(uri: Uri): Boolean {
        return try {
            val type = contentResolver.getType(uri) ?: ""
            val ext = when {
                type.contains("gif") -> "gif"
                type.contains("png") -> "png"
                type.contains("webp") -> "webp"
                else -> "jpg"
            }
            val out = File(prefs.stickerDir, "s_${System.currentTimeMillis()}_${(0..9999).random()}.$ext")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(out).use { output -> input.copyTo(output) }
            } ?: return false
            out.exists() && out.length() > 0
        } catch (e: Throwable) {
            false
        }
    }
}
