package com.example.scikeyboard

import android.graphics.Color
import android.graphics.Typeface

/**
 * কীবোর্ডের থিম। key/func = 0 হলে কী-র পেছনে রং নেই (ফ্ল্যাট), যেমন ছবির থিমে।
 */
data class KeyTheme(
    val id: String,
    val name: String,
    val bg: Int,
    val key: Int,
    val func: Int,
    val pressed: Int,
    val accent: Int,
    val text: Int,
    val funcText: Int,
    val hint: Int,
    val image: Boolean = false
)

object Themes {

    private fun c(s: String) = Color.parseColor(s)

    val all: List<KeyTheme> = listOf(
        KeyTheme(
            "dark", "Dark",
            c("#1B1C20"), c("#3A3C43"), c("#2A2C32"), c("#5A5D66"),
            c("#3D7BFF"), c("#F2F3F5"), c("#F2F3F5"), c("#9AA0AA")
        ),
        KeyTheme(
            "light", "Light",
            c("#E8EAED"), c("#FFFFFF"), c("#CDD1D6"), c("#B0B5BC"),
            c("#1A73E8"), c("#202124"), c("#202124"), c("#6B7078")
        ),
        KeyTheme(
            "amoled", "AMOLED black",
            c("#000000"), c("#1A1A1A"), c("#101010"), c("#3A3A3A"),
            c("#3D7BFF"), c("#FFFFFF"), c("#FFFFFF"), c("#8A8F98")
        ),
        KeyTheme(
            "blue", "Ocean blue",
            c("#0D1B2A"), c("#1B3A5C"), c("#14304D"), c("#2F5F94"),
            c("#4FC3F7"), c("#E8F1FA"), c("#E8F1FA"), c("#8FB2D0")
        ),
        KeyTheme(
            "green", "Green & red",
            c("#0F2A22"), 0, 0, c("#44FFFFFF"),
            c("#F42A41"), c("#F2F5F3"), c("#FF3B3B"), c("#A9BDB4")
        ),
        KeyTheme(
            "photo", "My photo",
            c("#000000"), 0, 0, c("#44FFFFFF"),
            c("#F42A41"), c("#FFFFFF"), c("#FF5252"), c("#D8D8D8"),
            image = true
        )
    )

    fun byId(id: String): KeyTheme = all.firstOrNull { it.id == id } ?: all[0]
}

object Fonts {

    data class FontOpt(val id: String, val name: String)

    val all: List<FontOpt> = listOf(
        FontOpt("default", "Default"),
        FontOpt("serif", "Serif"),
        FontOpt("mono", "Monospace"),
        FontOpt("casual", "Casual"),
        FontOpt("cursive", "Cursive"),
        FontOpt("condensed", "Condensed")
    )

    fun typeface(id: String): Typeface = when (id) {
        "serif" -> Typeface.SERIF
        "mono" -> Typeface.MONOSPACE
        "casual" -> Typeface.create("casual", Typeface.NORMAL)
        "cursive" -> Typeface.create("cursive", Typeface.NORMAL)
        "condensed" -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        else -> Typeface.DEFAULT
    }
}
