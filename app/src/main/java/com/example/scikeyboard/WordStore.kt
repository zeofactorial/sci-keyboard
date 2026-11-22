package com.example.scikeyboard

import android.content.Context

/**
 * শব্দ সাজেশনের ভান্ডার।
 * - seed: ইংরেজি + বাংলিশের শুরুর শব্দ (সংরক্ষণ হয় না, প্রতিবার লোড হয়)
 * - learned: আপনার টাইপ করা শব্দ (ফোনের ভেতরে জমা থাকে, কোথাও পাঠানো হয় না)
 * - bigrams: কোন শব্দের পর কোন শব্দ লেখেন (পরের শব্দের সাজেশনের জন্য)
 */
class WordStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("learned_words", Context.MODE_PRIVATE)

    private val seed = HashMap<String, Int>()
    private val counts = HashMap<String, Int>()
    private val pairs = HashMap<String, Int>()
    private var unsaved = 0

    init {
        loadSeed()
        load()
    }

    private fun loadSeed() {
        val en = SeedWords.english
        val n = en.size
        for ((i, w) in en.withIndex()) {
            if (w.isEmpty()) continue
            val score = when {
                i < n / 18 -> 5
                i < n / 5 -> 4
                i < n / 2 -> 3
                i < n * 3 / 4 -> 2
                else -> 1
            }
            seed[w] = score
        }
        for (w in SeedWords.banglish) {
            if (w.isNotEmpty()) seed[w] = 4
        }
    }

    private fun load() {
        val raw = prefs.getString(KEY, "") ?: ""
        for (line in raw.split('\n')) {
            val i = line.lastIndexOf('\t')
            if (i <= 0) continue
            val n = line.substring(i + 1).toIntOrNull() ?: continue
            counts[line.substring(0, i)] = n
        }
        val rawPairs = prefs.getString(KEY_PAIRS, "") ?: ""
        for (line in rawPairs.split('\n')) {
            val i = line.lastIndexOf('\t')
            if (i <= 0) continue
            val n = line.substring(i + 1).toIntOrNull() ?: continue
            pairs[line.substring(0, i)] = n
        }
    }

    fun reload() {
        counts.clear()
        pairs.clear()
        unsaved = 0
        load()
    }

    private fun isWord(w: String) = w.length in 2..32 && w.all { it.isLetter() }

    /** একটি সম্পূর্ণ শব্দ শেখে। [prev] দেওয়া থাকলে "prev → word" জোড়াও শেখে। */
    fun learn(word: String, prev: String? = null) {
        val w = word.lowercase()
        if (!isWord(w)) return
        counts[w] = (counts[w] ?: 0) + 1
        if (counts.size > MAX_WORDS) trim()
        if (prev != null) {
            val p = prev.lowercase()
            if (isWord(p)) {
                val k = p + " " + w
                pairs[k] = (pairs[k] ?: 0) + 1
                if (pairs.size > MAX_PAIRS) trimPairs()
            }
        }
        unsaved++
        if (unsaved >= 5) save()
    }

    private fun score(w: String): Int = (seed[w] ?: 0) + (counts[w] ?: 0) * 10

    /** [prefix] দিয়ে শুরু হওয়া সবচেয়ে সম্ভাব্য শব্দ। */
    fun suggest(prefix: String, limit: Int): List<String> {
        val p = prefix.lowercase()
        val found = HashMap<String, Int>()
        for (w in seed.keys) if (w.startsWith(p) && w != p) found[w] = score(w)
        for (w in counts.keys) if (w.startsWith(p) && w != p) found[w] = score(w)
        return found.entries
            .sortedWith(
                compareByDescending<Map.Entry<String, Int>> { it.value }
                    .thenBy { it.key.length }
                    .thenBy { it.key }
            )
            .take(limit)
            .map { it.key }
    }

    /** [prev] শব্দের পরে সাধারণত কোন শব্দ আসে (আপনার ইতিহাস থেকে)। */
    fun nextWords(prev: String?, limit: Int): List<String> {
        val result = ArrayList<String>()
        if (prev != null) {
            val p = prev.lowercase() + " "
            pairs.entries
                .filter { it.key.startsWith(p) }
                .sortedByDescending { it.value }
                .forEach { if (result.size < limit) result.add(it.key.substring(p.length)) }
        }
        if (result.size < limit) {
            counts.entries
                .sortedByDescending { it.value }
                .forEach { if (result.size < limit && it.key !in result) result.add(it.key) }
        }
        if (result.size < limit) {
            for (s in STARTERS) if (result.size < limit && s !in result) result.add(s)
        }
        return result
    }

    fun save() {
        if (unsaved == 0) return
        val sb = StringBuilder()
        for ((k, v) in counts) sb.append(k).append('\t').append(v).append('\n')
        val sp = StringBuilder()
        for ((k, v) in pairs) sp.append(k).append('\t').append(v).append('\n')
        prefs.edit().putString(KEY, sb.toString()).putString(KEY_PAIRS, sp.toString()).apply()
        unsaved = 0
    }

    fun clear() {
        counts.clear()
        pairs.clear()
        unsaved = 0
        prefs.edit().remove(KEY).remove(KEY_PAIRS).apply()
    }

    private fun trim() {
        val keep = counts.entries
            .sortedByDescending { it.value }
            .take(MAX_WORDS - 500)
            .map { it.key }
            .toSet()
        counts.keys.retainAll(keep)
    }

    private fun trimPairs() {
        val keep = pairs.entries
            .sortedByDescending { it.value }
            .take(MAX_PAIRS - 2000)
            .map { it.key }
            .toSet()
        pairs.keys.retainAll(keep)
    }

    private companion object {
        const val KEY = "data"
        const val KEY_PAIRS = "pairs"
        const val MAX_WORDS = 5000
        const val MAX_PAIRS = 20000
        val STARTERS = listOf("ki", "ami", "the", "i", "tumi", "what", "how")
    }
}
