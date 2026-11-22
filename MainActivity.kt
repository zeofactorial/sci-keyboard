package com.example.scikeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/** সেটআপ স্ক্রিন: কীবোর্ড চালু করা, বেছে নেওয়া এবং টেস্ট করার জায়গা। */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        text("Scientific Keyboard", 26f, bold = true)
        text("a–z কীবোর্ড + MS Word equation-এর মতো গাণিতিক চিহ্ন।", 15f, bottom = 20)

        text("ধাপ ১", 16f, bold = true, bottom = 4)
        button("কীবোর্ড চালু করুন (Settings খুলবে)") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        text("ধাপ ২", 16f, bold = true, bottom = 4)
        button("কীবোর্ড বেছে নিন") {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showInputMethodPicker()
        }

        text("ধাপ ৩: এখানে টাইপ করে টেস্ট করুন", 16f, bold = true, bottom = 4)
        col.addView(
            EditText(this).apply {
                hint = "এখানে টাইপ করুন (∑π চেপে α, β, √, ≤ ইত্যাদি চেষ্টা করুন)"
                minLines = 3
                gravity = android.view.Gravity.TOP
            },
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(20) }
        )

        text("কীভাবে ব্যবহার করবেন", 16f, bold = true, bottom = 4)
        text(
            "• ∑π বাটন → সব গাণিতিক চিহ্নের পেজ (Greek, Relations, Calculus, Arrows, Accents…)\n" +
                "• উপরের স্ট্রিপে x² / x₂ চাপলে পরের টাইপ করা অক্ষর/সংখ্যা সুপারস্ক্রিপ্ট/সাবস্ক্রিপ্ট হবে\n" +
                "• যেকোনো চিহ্নের বাটন চাপলেই সেটা সরাসরি টাইপ হবে, অন্য অক্ষরের মতো\n" +
                "• ⇧ দুইবার চাপলে Caps Lock\n" +
                "• ⌫ চেপে ধরলে ক্রমাগত মুছবে",
            14f
        )

        setContentView(ScrollView(this).apply { addView(col) })
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
