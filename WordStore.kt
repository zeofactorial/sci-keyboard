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

    /** সব পরিচিত শব্দ (সোয়াইপ টাইপিংয়ের জন্য)। */
    fun allWords(): Collection<String> {
        val all = HashSet<String>(seed.size + counts.size)
        all.addAll(seed.keys)
        all.addAll(counts.keys)
        return all
    }

    fun scoreOf(w: String): Int = score(w)

    /** শব্দটা কি ডিকশনারিতে (বা বারবার টাইপ করা) আছে? */
    fun isKnown(w: String): Boolean {
        val l = w.lowercase()
        return seed.containsKey(l) || (counts[l] ?: 0) >= 2
    }

    /**
     * ভুল বানানের জন্য সবচেয়ে সম্ভাব্য সঠিক শব্দ (১টা অক্ষর ভুল/বাদ/বাড়তি/উল্টো হলে)।
     * নিশ্চিত না হলে null।
     */
    fun correct(word: String): String? {
        val w = word.lowercase()
        if (w.length < 3 || isKnown(w)) return null
        var best: String? = null
        var bestScore = -1
        var bestSameLen = false
        fun consider(c: String) {
            if (c == w || kotlin.math.abs(c.length - w.length) > 1) return
            if (c[0] != w[0]) return
            if (!withinOneEdit(w, c)) return
            val sc = score(c)
            if (sc < 20 && (seed[c] ?: 0) < 3) return
            val same = c.length == w.length
            if (best == null || (same && !bestSameLen) || (same == bestSameLen && sc > bestScore)) {
                best = c
                bestScore = sc
                bestSameLen = same
            }
        }
        for (c in seed.keys) consider(c)
        for (c in counts.keys) consider(c)
        return best
    }

    /** a আর b-র মধ্যে ১টার বেশি সম্পাদনা (বদল/বাদ/যোগ/পাশাপাশি উল্টো) লাগে কি না। */
    private fun withinOneEdit(a: String, b: String): Boolean {
        if (a == b) return true
        val la = a.length
        val lb = b.length
        if (kotlin.math.abs(la - lb) > 1) return false
        if (la == lb) {
            var i = 0
            while (i < la && a[i] == b[i]) i++
            if (i == la) return true
            // একটা অক্ষর বদল
            if (a.substring(i + 1) == b.substring(i + 1)) return true
            // পাশাপাশি দুটো অক্ষর উল্টো
            if (i + 1 < la && a[i] == b[i + 1] && a[i + 1] == b[i] &&
                a.substring(i + 2) == b.substring(i + 2)
            ) return true
            return false
        }
        val (s, l) = if (la < lb) Pair(a, b) else Pair(b, a)
        var i = 0
        while (i < s.length && s[i] == l[i]) i++
        return s.substring(i) == l.substring(i + 1)
    }

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
