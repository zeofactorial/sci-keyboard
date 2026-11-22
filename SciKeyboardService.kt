package com.example.scikeyboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

/**
 * Scientific Keyboard: QWERTY (a–z) + MS Word equation-এর মতো গাণিতিক চিহ্ন।
 * UI পুরোটাই কোডে তৈরি (XML/Compose ছাড়া), তাই ফাইল কম এবং বদলানো সহজ।
 */
class SciKeyboardService : InputMethodService() {

    private enum class Page { LETTERS, NUMBERS, SYMBOLS }
    private enum class Script { NONE, SUP, SUB }

    private var page = Page.LETTERS
    private var shift = false
    private var caps = false
    private var lastShiftTap = 0L
    private var script = Script.NONE
    private var category = 0

    // শব্দ সাজেশন
    private var learning = true
    private var suggestions: List<String> = emptyList()
    private var strip: LinearLayout? = null
    private lateinit var store: WordStore

    private lateinit var root: LinearLayout
    private val handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------- lifecycle

    override fun onCreate() {
        super.onCreate()
        store = WordStore(this)
    }

    override fun onDestroy() {
        store.save()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(C_BG)
            setPadding(dp(3), dp(3), dp(3), dp(3))
        }
        render()
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        learning = shouldLearn(info?.inputType ?: 0)
        suggestions = emptyList()
        if (!restarting) {
            store.reload()
            shift = false
            caps = false
            script = Script.NONE
            val cls = (info?.inputType ?: 0) and InputType.TYPE_MASK_CLASS
            page = if (cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE) {
                Page.NUMBERS
            } else {
                Page.LETTERS
            }
        }
        if (::root.isInitialized) render()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        handler.removeCallbacksAndMessages(null)
        store.save()
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd
        )
        if (::root.isInitialized) updateSuggestions()
    }

    // ---------------------------------------------------------------- rendering

    private fun go(p: Page) {
        page = p
        suggestions = emptyList()
        render()
        updateSuggestions()
    }

    private fun render() {
        root.removeAllViews()
        when (page) {
            Page.LETTERS -> {
                root.addView(newStrip())
                buildLetters()
            }
            Page.NUMBERS -> {
                root.addView(newStrip())
                buildNumbers()
            }
            Page.SYMBOLS -> {
                strip = null
                buildSymbols()
            }
        }
    }

    private fun buildLetters() {
        val up = shift || caps
        root.addView(rowOf(charKeys("1234567890", false)))
        root.addView(rowOf(charKeys("qwertyuiop", up)))
        root.addView(rowOf(listOf(spacer(0.5f)) + charKeys("asdfghjkl", up) + listOf(spacer(0.5f))))

        val r3 = ArrayList<View>()
        r3.add(shiftKey())
        r3.addAll(charKeys("zxcvbnm", up))
        r3.add(backspaceKey(1.5f))
        root.addView(rowOf(r3))

        root.addView(
            rowOf(
                listOf(
                    key("?123", weight = 1.3f, color = C_FUNC, textSp = 14f) { go(Page.NUMBERS) },
                    key("∑π", weight = 1.3f, color = C_FUNC, textSp = 16f) { go(Page.SYMBOLS) },
                    key(",") { typeKey(",") },
                    key("-") { typeKey("-") },
                    spaceKey(3f),
                    key(".") { typeKey(".") },
                    enterKey(1.3f)
                )
            )
        )
    }

    private fun buildNumbers() {
        root.addView(rowOf(charKeys("1234567890", false)))
        root.addView(rowOf(charKeys("+-×÷=()/\\^", false)))
        root.addView(rowOf(charKeys("<>≤≥≠±°%_!", false)))
        root.addView(
            rowOf(
                listOf(
                    key("ABC", weight = 1.3f, color = C_FUNC, textSp = 14f) { go(Page.LETTERS) },
                    key("∑π", weight = 1.3f, color = C_FUNC, textSp = 16f) { go(Page.SYMBOLS) },
                    key(",") { typeKey(",") },
                    spaceKey(3f),
                    key(".") { typeKey(".") },
                    key("?") { typeKey("?") },
                    backspaceKey(1.3f),
                    enterKey(1.3f)
                )
            )
        )
    }

    private fun buildSymbols() {
        // ক্যাটাগরি ট্যাব
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        SymbolData.categories.forEachIndexed { i, cat ->
            val tab = key(
                cat.name,
                color = if (i == category) C_ACCENT else C_FUNC,
                textSp = 14f
            ) {
                category = i
                render()
            }
            tab.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
            tab.setPadding(dp(12), 0, dp(12), 0)
            tabs.addView(tab)
        }
        root.addView(
            HorizontalScrollView(this).apply {
                isHorizontalScrollBarEnabled = false
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(STRIP_H)
                )
                addView(tabs)
            }
        )

        // চিহ্নের গ্রিড (৮ কলাম, স্ক্রল করা যায়)
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        SymbolData.categories[category].items.chunked(COLS).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { sym ->
                cells.add(key(sym.label, textSp = 20f) { commitSymbol(sym.insert) })
            }
            repeat(COLS - chunk.size) { cells.add(spacer(1f)) }
            grid.addView(rowOf(cells))
        }
        root.addView(
            ScrollView(this).apply {
                isVerticalScrollBarEnabled = false
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(ROW_H * 4)
                )
                addView(grid)
            }
        )

        root.addView(
            rowOf(
                listOf(
                    key("ABC", weight = 1.5f, color = C_FUNC, textSp = 14f) { go(Page.LETTERS) },
                    key("?123", weight = 1.5f, color = C_FUNC, textSp = 14f) { go(Page.NUMBERS) },
                    spaceKey(4f),
                    backspaceKey(1.5f),
                    enterKey(1.5f)
                )
            )
        )
    }

    /** উপরের দ্রুত-চিহ্নের স্ট্রিপ: x² / x₂ মোড বাটন + বহুল ব্যবহৃত চিহ্ন। */
    private fun quickStrip(): View {
        val inner = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun addKey(v: TextView) {
            v.layoutParams = LinearLayout.LayoutParams(dp(46), ViewGroup.LayoutParams.MATCH_PARENT)
                .apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
            inner.addView(v)
        }
        addKey(key("x²", color = if (script == Script.SUP) C_ACCENT else C_FUNC) { toggleScript(Script.SUP) })
        addKey(key("x₂", color = if (script == Script.SUB) C_ACCENT else C_FUNC) { toggleScript(Script.SUB) })
        QUICK.forEach { s -> addKey(key(s) { commitSymbol(s) }) }

        return HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(STRIP_H)
            )
            addView(inner)
        }
    }

    // ---------------------------------------------------------------- key factories

    private fun charKeys(chars: String, upper: Boolean): List<View> =
        chars.map { ch ->
            val t = if (upper) ch.uppercaseChar().toString() else ch.toString()
            key(t) { typeKey(t) }
        }

    private fun shiftKey(): View {
        val active = shift || caps
        return key(
            if (caps) "⇪" else "⇧",
            weight = 1.5f,
            color = if (active) C_ACCENT else C_FUNC
        ) {
            val now = System.currentTimeMillis()
            if (now - lastShiftTap < 400) {
                caps = true
                shift = false
            } else if (caps) {
                caps = false
                shift = false
            } else {
                shift = !shift
            }
            lastShiftTap = now
            render()
        }
    }

    private fun spaceKey(weight: Float): View =
        key("space", weight = weight, textSp = 13f, textColor = C_HINT) { pressSpace() }

    private fun enterKey(weight: Float): View =
        key("⏎", weight = weight, color = C_FUNC) { pressEnter() }

    private fun backspaceKey(weight: Float): View {
        val v = makeKey("⌫", weight, C_FUNC, 18f, C_TEXT)
        val repeater = object : Runnable {
            override fun run() {
                backspace()
                handler.postDelayed(this, 50)
            }
        }
        v.setOnTouchListener { view, ev ->
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    view.isPressed = true
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    backspace()
                    handler.postDelayed(repeater, 400)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.isPressed = false
                    handler.removeCallbacks(repeater)
                }
            }
            true
        }
        return v
    }

    private fun key(
        label: String,
        weight: Float = 1f,
        color: Int = C_KEY,
        textSp: Float = 18f,
        textColor: Int = C_TEXT,
        onTap: () -> Unit
    ): TextView {
        val v = makeKey(label, weight, color, textSp, textColor)
        v.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onTap()
        }
        return v
    }

    private fun makeKey(label: String, weight: Float, color: Int, textSp: Float, textColor: Int): TextView =
        TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            isSingleLine = true
            isClickable = true
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSp)
            background = keyBackground(color)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
                .apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
        }

    private fun keyBackground(color: Int) = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), roundRect(C_PRESSED))
        addState(intArrayOf(), roundRect(color))
    }

    private fun roundRect(color: Int) = GradientDrawable().apply {
        cornerRadius = dp(6).toFloat()
        setColor(color)
    }

    private fun spacer(weight: Float): View =
        View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
        }

    private fun rowOf(items: List<View>): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(ROW_H))
            items.forEach { addView(it) }
        }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    // ---------------------------------------------------------------- input actions

    private fun commit(s: String) {
        currentInputConnection?.commitText(s, 1)
    }

    private fun toggleScript(s: Script) {
        script = if (script == s) Script.NONE else s
        render()
        updateSuggestions()
    }

    // ---------------------------------------------------------------- word suggestions

    /** পাসওয়ার্ড, ইমেইল, URL বা "no suggestions" ঘরে শেখা/সাজেশন বন্ধ থাকে। */
    private fun shouldLearn(type: Int): Boolean {
        val cls = type and InputType.TYPE_MASK_CLASS
        val variation = type and InputType.TYPE_MASK_VARIATION
        if (cls == InputType.TYPE_CLASS_TEXT) {
            if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
                variation == InputType.TYPE_TEXT_VARIATION_URI
            ) return false
            if ((type and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0) return false
        }
        if (cls == InputType.TYPE_CLASS_NUMBER &&
            variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
        ) return false
        return true
    }

    /** কার্সরের ঠিক আগের (অসম্পূর্ণ) শব্দ। */
    private fun currentPrefix(): String {
        val before = currentInputConnection?.getTextBeforeCursor(32, 0) ?: return ""
        var i = before.length
        while (i > 0 && before[i - 1].isLetter()) i--
        return before.substring(i)
    }

    private fun learnCurrent() {
        if (!learning || script != Script.NONE) return
        val w = currentPrefix()
        if (w.length >= 2) store.learn(w)
    }

    private fun capLike(word: String, prefix: String): String = when {
        prefix.length > 1 && prefix.all { it.isUpperCase() } -> word.uppercase()
        prefix.isNotEmpty() && prefix[0].isUpperCase() ->
            word.replaceFirstChar { it.uppercaseChar() }
        else -> word
    }

    private fun updateSuggestions() {
        val next: List<String> =
            if (!learning || script != Script.NONE || page != Page.LETTERS) {
                emptyList()
            } else {
                val p = currentPrefix()
                if (p.isEmpty()) emptyList() else store.suggest(p, 3).map { capLike(it, p) }
            }
        if (next != suggestions) {
            suggestions = next
            fillStrip()
        }
    }

    private fun newStrip(): View {
        val holder = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(STRIP_H)
            )
        }
        strip = holder
        fillStrip()
        return holder
    }

    private fun fillStrip() {
        val holder = strip ?: return
        holder.removeAllViews()
        holder.addView(
            if (page == Page.LETTERS && suggestions.isNotEmpty()) suggestionStrip() else quickStrip()
        )
    }

    private fun suggestionStrip(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(STRIP_H)
            )
        }
        suggestions.forEach { w ->
            row.addView(key(w, color = C_FUNC, textSp = 17f) { acceptSuggestion(w) })
        }
        repeat(3 - suggestions.size) { row.addView(spacer(1f)) }
        return row
    }

    private fun acceptSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        val prefix = currentPrefix()
        ic.beginBatchEdit()
        ic.deleteSurroundingText(prefix.length, 0)
        ic.commitText("$word ", 1)
        ic.endBatchEdit()
        if (learning) store.learn(word)
        updateSuggestions()
    }

    /** চিহ্ন বসানোর আগে চলতি শব্দটা শিখে নেয়। */
    private fun commitSymbol(s: String) {
        learnCurrent()
        commit(s)
        updateSuggestions()
    }

    /** অক্ষর/সংখ্যা টাইপ; x²/x₂ মোড চালু থাকলে সুপার/সাবস্ক্রিপ্টে রূপান্তর করে। */
    private fun typeKey(s: String) {
        val out = when (script) {
            Script.SUP -> s.map { (SUPER[it] ?: it).toString() }.joinToString("")
            Script.SUB -> s.map { (SUB[it] ?: it).toString() }.joinToString("")
            Script.NONE -> s
        }
        if (out.isNotEmpty() && !out[0].isLetter()) learnCurrent()
        commit(out)
        updateSuggestions()
        if (shift && !caps) {
            shift = false
            render()
        }
    }

    private fun pressSpace() {
        learnCurrent()
        commit(" ")
        updateSuggestions()
    }

    private fun pressEnter() {
        learnCurrent()
        val ic = currentInputConnection ?: return
        val ei = currentInputEditorInfo
        val action = (ei?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION
        val noEnterAction = ((ei?.imeOptions ?: 0) and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0
        if (!noEnterAction &&
            action != EditorInfo.IME_ACTION_NONE &&
            action != EditorInfo.IME_ACTION_UNSPECIFIED
        ) {
            ic.performEditorAction(action)
        } else {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
        }
        updateSuggestions()
    }

    private fun backspace() {
        doBackspace()
        updateSuggestions()
    }

    private fun doBackspace() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
            return
        }
        val before = ic.getTextBeforeCursor(2, 0)
        when {
            before.isNullOrEmpty() -> sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
            before.length == 2 &&
                Character.isHighSurrogate(before[0]) &&
                Character.isLowSurrogate(before[1]) -> ic.deleteSurroundingText(2, 0)
            else -> ic.deleteSurroundingText(1, 0)
        }
    }

    // ---------------------------------------------------------------- constants

    private companion object {
        const val ROW_H = 46      // dp: প্রতিটি সারির উচ্চতা (মার্জিনসহ)
        const val STRIP_H = 40    // dp: উপরের স্ট্রিপ
        const val COLS = 8        // সিম্বল পেজের কলাম

        val C_BG = Color.parseColor("#1B1C20")
        val C_KEY = Color.parseColor("#3A3C43")
        val C_FUNC = Color.parseColor("#2A2C32")
        val C_PRESSED = Color.parseColor("#5A5D66")
        val C_ACCENT = Color.parseColor("#3D7BFF")
        val C_TEXT = Color.parseColor("#F2F3F5")
        val C_HINT = Color.parseColor("#9AA0AA")

        val QUICK = listOf(
            "π", "√", "∑", "∫", "±", "×", "÷", "≤", "≥", "≠", "≈", "∞", "°",
            "θ", "α", "β", "Δ", "∂", "→", "²", "³", "½"
        )

        private fun pairs(from: String, to: String): Map<Char, Char> = from.zip(to).toMap()

        val SUPER: Map<Char, Char> = pairs(
            "0123456789+-=()abcdefghijklmnoprstuvwxyzABDEGHIJKLMNOPRTUW",
            "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖʳˢᵗᵘᵛʷˣʸᶻᴬᴮᴰᴱᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾᴿᵀᵁᵂ"
        )

        val SUB: Map<Char, Char> = pairs(
            "0123456789+-=()aehijklmnoprstuvx",
            "₀₁₂₃₄₅₆₇₈₉₊₋₌₍₎ₐₑₕᵢⱼₖₗₘₙₒₚᵣₛₜᵤᵥₓ"
        )
    }
}
