package com.example.scikeyboard

/** বাংলা কীবোর্ড লেআউট: ৩টা স্তর (ব্যঞ্জন, স্বর ও কার, সংখ্যা ও যতিচিহ্ন)। */
object BanglaData {

    class Layer(val label: String, val rows: List<List<String>>)

    private fun row(s: String): List<String> = s.trim().split(' ').filter { it.isNotEmpty() }

    val layers: List<Layer> = listOf(
        Layer(
            "ক",
            listOf(
                row("ক খ গ ঘ ঙ চ ছ জ ঝ ঞ ট"),
                row("ঠ ড ঢ ণ ত থ দ ধ ন প ফ"),
                row("ব ভ ম য র ল শ ষ স হ DDA")
            )
        ),
        Layer(
            "অ",
            listOf(
                row("অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ"),
                row("া ি ী ু ূ ৃ ে ৈ ো ৌ ্"),
                row("ং ঃ ঁ ৎ DDHA YYA ় ZWNJ ZWJ । ,")
            )
        ),
        Layer(
            "১২৩",
            listOf(
                row("০ ১ ২ ৩ ৪ ৫ ৬ ৭ ৮ ৯ ৳"),
                row("। , . ? ! : ; ' \" - ("),
                row(") / + = * % @ # & _ ~")
            )
        )
    )

    /** কার/হসন্ত চিহ্নের আগে ◌ দেখিয়ে বোঝানো হয়, কিন্তু টাইপ হয় শুধু চিহ্নটাই। */
    private val MARKS = "া ি ী ু ূ ৃ ে ৈ ো ৌ ্ ঁ ়".split(' ').toSet()

    /** বাটনে যা দেখায়। */
    fun display(s: String): String = when (s) {
        "DDA" -> "\u09DC"
        "DDHA" -> "\u09DD"
        "YYA" -> "\u09DF"
        "ZWNJ" -> "ZWNJ"
        "ZWJ" -> "ZWJ"
        else -> if (s in MARKS) "◌$s" else s
    }

    /** টেক্সট ফিল্ডে যা বসে। */
    fun insert(s: String): String = when (s) {
        "DDA" -> "\u09DC"
        "DDHA" -> "\u09DD"
        "YYA" -> "\u09DF"
        "ZWNJ" -> "\u200C"
        "ZWJ" -> "\u200D"
        else -> s
    }
}
