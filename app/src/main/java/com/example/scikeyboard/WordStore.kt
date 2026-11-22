package com.example.scikeyboard

import android.content.Context

/**
 * আগে টাইপ করা শব্দ মনে রাখে এবং সাজেশন দেয়।
 * শব্দগুলো শুধু ফোনের ভেতরে থাকে, কোথাও পাঠানো হয় না।
 */
class WordStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("learned_words", Context.MODE_PRIVATE)
    private val counts = HashMap<String, Int>()
    private var unsaved = 0

    init {
        load()
    }

    private fun load() {
        val raw = prefs.getString(KEY, "") ?: ""
        for (line in raw.split('\n')) {
            val i = line.lastIndexOf('\t')
            if (i <= 0) continue
            val n = line.substring(i + 1).toIntOrNull() ?: continue
            counts[line.substring(0, i)] = n
        }
    }

    fun reload() {
        counts.clear()
        unsaved = 0
        load()
    }

    /** একটি সম্পূর্ণ শব্দ শেখে (ছোট হাতের অক্ষরে জমা হয়)। */
    fun learn(word: String) {
        val w = word.lowercase()
        if (w.length < 2 || w.length > 32 || !w.all { it.isLetter() }) return
        counts[w] = (counts[w] ?: 0) + 1
        if (counts.size > MAX_WORDS) trim()
        unsaved++
        if (unsaved >= 5) save()
    }

    /** [prefix] দিয়ে শুরু হওয়া সবচেয়ে বেশি ব্যবহৃত শব্দ। */
    fun suggest(prefix: String, limit: Int): List<String> {
        val p = prefix.lowercase()
        return counts.entries
            .filter { it.key.startsWith(p) && it.key != p }
            .sortedWith(
                compareByDescending<Map.Entry<String, Int>> { it.value }
                    .thenBy { it.key.length }
            )
            .take(limit)
            .map { it.key }
    }

    fun save() {
        if (unsaved == 0) return
        val sb = StringBuilder()
        for ((k, v) in counts) {
            sb.append(k).append('\t').append(v).append('\n')
        }
        prefs.edit().putString(KEY, sb.toString()).apply()
        unsaved = 0
    }

    fun clear() {
        counts.clear()
        unsaved = 0
        prefs.edit().remove(KEY).apply()
    }

    private fun trim() {
        val keep = counts.entries
            .sortedByDescending { it.value }
            .take(MAX_WORDS - 500)
            .map { it.key }
            .toSet()
        counts.keys.retainAll(keep)
    }

    private companion object {
        const val KEY = "data"
        const val MAX_WORDS = 5000
    }
}
