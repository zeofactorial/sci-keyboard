package com.example.scikeyboard

import android.view.inputmethod.InputConnection

/**
 * ইংরেজি অক্ষরে বাংলা লেখা (Avro-এর মতো ফোনেটিক)।
 * এই ফাইল একা কাজ করে, অন্য কোনো ফাইলের ওপর নির্ভর করে না।
 *
 * নিয়ম (সংক্ষেপে):
 *  - a=া/আ  i=ি/ই  I=ী/ঈ  u=ু/উ  U=ূ/ঊ  e=ে/এ  O=ো/ও  oi=ৈ/ঐ  ou=ৌ/ঔ  rri=ৃ/ঋ
 *  - o: শব্দের শুরুতে অ; ব্যঞ্জনের পরে শব্দের শেষে ো (bhalo=ভালো), মাঝে থাকলে কিছু না (kemon=কেমন)
 *  - k=ক kh=খ g=গ gh=ঘ Ng=ঙ c/ch=চ chh=ছ j=জ jh=ঝ T=ট Th=ঠ D=ড Dh=ঢ N=ণ t=ত th=থ d=দ dh=ধ
 *    n=ন p=প ph/f=ফ b=ব bh/v=ভ m=ম z=য r=র l=ল sh=শ S=ষ s=স h=হ R=ড় Rh=ঢ় y=য়
 *  - ng=ং  H=ঃ  M=ঁ  q=হসন্ত (k q t = ক্ত)  rr=র্  y/w ব্যঞ্জনের পরে ্য/্ব
 *  - জোড়া: kk gg cc jj tt dd nn pp bb mm ll ss, kkh=ক্ষ, nt nd ndh nth mp mb st sth nj kt, tr dr pr br gr kr shr
 */
object BanglaPhonetic {

    private val VOWELS: Map<String, Pair<String, String>> = mapOf(
        "rri" to Pair("ঋ", "ৃ"),
        "oi" to Pair("ঐ", "ৈ"),
        "OI" to Pair("ঐ", "ৈ"),
        "ou" to Pair("ঔ", "ৌ"),
        "OU" to Pair("ঔ", "ৌ"),
        "aa" to Pair("আ", "া"),
        "a" to Pair("আ", "া"),
        "ii" to Pair("ঈ", "ী"),
        "ee" to Pair("ঈ", "ী"),
        "I" to Pair("ঈ", "ী"),
        "i" to Pair("ই", "ি"),
        "uu" to Pair("ঊ", "ূ"),
        "U" to Pair("ঊ", "ূ"),
        "u" to Pair("উ", "ু"),
        "e" to Pair("এ", "ে"),
        "O" to Pair("ও", "ো")
    )

    private val CONS: Map<String, String> = mapOf(
        // জোড়া অক্ষর
        "kkh" to "ক্ষ", "ndh" to "ন্ধ", "nth" to "ন্থ", "sth" to "স্থ", "shr" to "শ্র",
        "kk" to "ক্ক", "gg" to "গ্গ", "cc" to "চ্চ", "jj" to "জ্জ", "tt" to "ত্ত",
        "dd" to "দ্দ", "nn" to "ন্ন", "pp" to "প্প", "bb" to "ব্ব", "mm" to "ম্ম",
        "ll" to "ল্ল", "ss" to "স্স",
        "nt" to "ন্ত", "nd" to "ন্দ", "mp" to "ম্প", "mb" to "ম্ব", "st" to "স্ত",
        "nj" to "ঞ্জ", "kt" to "ক্ত",
        "tr" to "ত্র", "dr" to "দ্র", "pr" to "প্র", "br" to "ব্র", "gr" to "গ্র", "kr" to "ক্র",
        // দুই/তিন অক্ষরের ব্যঞ্জন
        "chh" to "ছ", "ch" to "চ", "kh" to "খ", "gh" to "ঘ", "jh" to "ঝ",
        "Th" to "ঠ", "Dh" to "ঢ", "th" to "থ", "dh" to "ধ", "ph" to "ফ", "bh" to "ভ",
        "sh" to "শ", "Rh" to "ঢ়", "Ng" to "ঙ",
        // এক অক্ষর
        "k" to "ক", "g" to "গ", "c" to "চ", "j" to "জ", "J" to "জ", "T" to "ট", "D" to "ড",
        "N" to "ণ", "t" to "ত", "d" to "দ", "n" to "ন", "p" to "প", "f" to "ফ", "b" to "ব",
        "v" to "ভ", "m" to "ম", "z" to "য", "r" to "র", "l" to "ল", "S" to "ষ", "s" to "স",
        "h" to "হ", "R" to "ড়", "x" to "ক্স"
    )

    private val SIGNS: Map<String, String> = mapOf(
        "ng" to "ং", "H" to "ঃ", "M" to "ঁ", "q" to "্", "rr" to "র্"
    )

    private const val MAX_LEN = 3

    /** ইংরেজি অক্ষরের একটি শব্দকে বাংলায় বদলায়। */
    fun convert(src: String): String {
        val out = StringBuilder()
        var afterCons = false
        var i = 0
        while (i < src.length) {
            var done = false
            var len = minOf(MAX_LEN, src.length - i)
            while (len >= 1 && !done) {
                val part = src.substring(i, i + len)
                val v = VOWELS[part]
                val c = CONS[part]
                val sign = SIGNS[part]
                when {
                    part == "o" -> {
                        val atEnd = i + len == src.length
                        if (!afterCons) {
                            out.append("অ")
                        } else if (atEnd) {
                            out.append("ো")
                        }
                        afterCons = false
                        done = true
                    }
                    v != null -> {
                        out.append(if (afterCons) v.second else v.first)
                        afterCons = false
                        done = true
                    }
                    part == "y" -> {
                        out.append(if (afterCons) "্য" else "য়")
                        afterCons = true
                        done = true
                    }
                    part == "w" -> {
                        if (afterCons) {
                            out.append("্ব")
                            afterCons = true
                        } else {
                            out.append("ও")
                            afterCons = false
                        }
                        done = true
                    }
                    c != null -> {
                        out.append(c)
                        afterCons = true
                        done = true
                    }
                    sign != null -> {
                        out.append(sign)
                        afterCons = false
                        done = true
                    }
                }
                if (done) i += len else len--
            }
            if (!done) {
                out.append(src[i])
                afterCons = false
                i++
            }
        }
        return out.toString()
    }
}

/**
 * কীবোর্ডের সাথে জোড়া লাগানোর অংশ: টাইপ করা ইংরেজি অক্ষর জমিয়ে রাখে এবং
 * বাংলা রূপ "composing" (নিচে দাগসহ) হিসেবে দেখায়। স্পেস/চিহ্ন চাপলে শব্দটা পাকা হয়।
 */
class BanglaInput {

    var enabled = false
        private set

    private val buf = StringBuilder()
    private var shown = ""

    /** বাংলা/English মোড বদলায়। */
    fun toggle(ic: InputConnection?) {
        finish(ic)
        enabled = !enabled
    }

    /** একটি ইংরেজি অক্ষর নেয়। মোড চালু থাকলে true ফেরত দেয় (অক্ষরটা আমরা সামলেছি)। */
    fun typeLetter(ic: InputConnection?, ch: String): Boolean {
        if (!enabled || ic == null) return false
        if (!consistent(ic)) {
            ic.finishComposingText()
            reset()
        }
        buf.append(ch)
        shown = BanglaPhonetic.convert(buf.toString())
        ic.setComposingText(shown, 1)
        return true
    }

    /** চলতি শব্দ পাকা করে (স্পেস, চিহ্ন বা অন্য কিছু বসানোর আগে ডাকতে হবে)। */
    fun finish(ic: InputConnection?) {
        if (buf.isNotEmpty()) {
            ic?.finishComposingText()
            reset()
        }
    }

    /** ব্যাকস্পেস: চলতি শব্দের শেষ ইংরেজি অক্ষর বাদ দেয়। সামলালে true। */
    fun backspace(ic: InputConnection?): Boolean {
        if (!enabled || ic == null || buf.isEmpty()) return false
        if (!consistent(ic)) {
            ic.finishComposingText()
            reset()
            return false
        }
        buf.setLength(buf.length - 1)
        if (buf.isEmpty()) {
            shown = ""
            ic.commitText("", 1)
        } else {
            shown = BanglaPhonetic.convert(buf.toString())
            ic.setComposingText(shown, 1)
        }
        return true
    }

    private fun reset() {
        buf.setLength(0)
        shown = ""
    }

    /** কার্সর অন্য জায়গায় সরে গেলে (বা লেখা বদলালে) জমানো অক্ষর বাদ দিই। */
    private fun consistent(ic: InputConnection): Boolean {
        if (buf.isEmpty()) return true
        val before = ic.getTextBeforeCursor(shown.length, 0)?.toString()
        return before == shown
    }
}
