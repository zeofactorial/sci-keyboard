package com.example.scikeyboard

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/** ছবির থিমের জন্য: ছবিটা কীবোর্ডের পেছনে center-crop করে আঁকে (আকার বদলায় না)। */
private class CropDrawable(private val bmp: Bitmap, private val scrim: Int) : Drawable() {
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)

    override fun draw(canvas: Canvas) {
        val b = bounds
        if (b.width() <= 0 || b.height() <= 0) return
        val scale = maxOf(b.width().toFloat() / bmp.width, b.height().toFloat() / bmp.height)
        val w = bmp.width * scale
        val h = bmp.height * scale
        val left = b.left + (b.width() - w) / 2f
        val top = b.top + (b.height() - h) / 2f
        canvas.save()
        canvas.clipRect(b)
        canvas.drawBitmap(bmp, null, RectF(left, top, left + w, top + h), paint)
        canvas.drawColor(scrim)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}

/**
 * Scientific Keyboard: QWERTY + গাণিতিক চিহ্ন + শব্দ সাজেশন + ইমোজি + ক্লিপবোর্ড
 * + টেক্সট এডিট + ভয়েস টাইপিং + থিম। UI পুরোটাই কোডে তৈরি।
 */
class SciKeyboardService : InputMethodService() {

    private enum class Page { LETTERS, NUMBERS, SYMBOLS, EMOJI, CLIP, EDIT, NUMPAD, THEME }
    private enum class Script { NONE, SUP, SUB }
    private enum class Kind { KEY, FUNC, FLAT, ACCENT }

    private var page = Page.LETTERS
    private var shift = false
    private var caps = false
    private var lastShiftTap = 0L
    private var script = Script.NONE
    private var category = 0
    private var emojiCat = 0
    private var toolsOpen = false
    private var selectMode = false

    // শব্দ সাজেশন
    private var learning = true
    private var suggestions: List<String> = emptyList()
    private var strip: LinearLayout? = null
    private lateinit var store: WordStore

    // সেটিং ও থিম
    private lateinit var prefs: Prefs
    private lateinit var skin: KeyTheme
    private var font: Typeface = Typeface.DEFAULT
    private var bgBitmap: Bitmap? = null
    private var bgStamp = 0L

    // ভয়েস টাইপিং
    private var recognizer: SpeechRecognizer? = null
    private var listening = false

    // ক্লিপবোর্ড ইতিহাস (শুধু মেমরিতে থাকে, ডিস্কে সেভ হয় না)
    private val clips = ArrayList<String>()
    private var clipboard: ClipboardManager? = null
    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener { onClipChanged() }

    private lateinit var root: LinearLayout
    private val handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------- lifecycle

    override fun onCreate() {
        super.onCreate()
        store = WordStore(this)
        prefs = Prefs(this)
        reloadSkin()
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.addPrimaryClipChangedListener(clipListener)
    }

    override fun onDestroy() {
        store.save()
        clipboard?.removePrimaryClipChangedListener(clipListener)
        recognizer?.destroy()
        recognizer = null
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(3), dp(3), dp(3), dp(3))
        }
        applyBackground()
        render()
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        learning = shouldLearn(info?.inputType ?: 0)
        suggestions = emptyList()
        if (!restarting) {
            store.reload()
            reloadSkin()
            shift = false
            caps = false
            script = Script.NONE
            toolsOpen = false
            selectMode = false
            val cls = (info?.inputType ?: 0) and InputType.TYPE_MASK_CLASS
            page = if (cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE) {
                Page.NUMBERS
            } else {
                Page.LETTERS
            }
        }
        if (::root.isInitialized) {
            applyBackground()
            render()
            updateSuggestions()
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        handler.removeCallbacksAndMessages(null)
        store.save()
        if (listening) {
            recognizer?.cancel()
            listening = false
        }
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

    // ---------------------------------------------------------------- theme

    private fun reloadSkin() {
        skin = Themes.byId(prefs.themeId)
        font = Fonts.typeface(prefs.fontId)
    }

    private fun applyBackground() {
        if (!::root.isInitialized) return
        val bmp = if (skin.image) loadBg() else null
        root.background = if (bmp != null) {
            CropDrawable(bmp, Color.argb(110, 0, 0, 0))
        } else {
            ColorDrawable(skin.bg)
        }
    }

    private fun loadBg(): Bitmap? {
        val f = prefs.bgFile
        if (!f.exists()) return null
        val stamp = f.lastModified()
        if (bgBitmap == null || stamp != bgStamp) {
            bgBitmap = try {
                BitmapFactory.decodeFile(f.path)
            } catch (e: Throwable) {
                null
            }
            bgStamp = stamp
        }
        return bgBitmap
    }

    private fun chooseTheme(id: String) {
        prefs.themeId = id
        reloadSkin()
        applyBackground()
        render()
    }

    private fun chooseFont(id: String) {
        prefs.fontId = id
        reloadSkin()
        render()
    }

    // ---------------------------------------------------------------- rendering

    private fun go(p: Page) {
        page = p
        toolsOpen = false
        suggestions = emptyList()
        render()
        updateSuggestions()
    }

    private fun render() {
        root.removeAllViews()
        strip = null
        when (page) {
            Page.LETTERS -> {
                root.addView(newStrip())
                buildLetters()
            }
            Page.NUMBERS -> {
                root.addView(newStrip())
                buildNumbers()
            }
            Page.SYMBOLS -> buildSymbols()
            Page.EMOJI -> buildEmoji()
            Page.CLIP -> buildClip()
            Page.EDIT -> buildEdit()
            Page.NUMPAD -> buildNumpad()
            Page.THEME -> buildThemePage()
        }
    }

    private fun buildLetters() {
        val up = shift || caps
        root.addView(rowOf(letterKeys("qwertyuiop", "1234567890", up)))
        root.addView(
            rowOf(
                listOf(spacer(0.5f)) +
                    letterKeys("asdfghjkl", "@#&*-!?()", up) +
                    listOf(spacer(0.5f))
            )
        )

        val r3 = ArrayList<View>()
        r3.add(shiftKey())
        r3.addAll(letterKeys("zxcvbnm", "\"×÷√':/", up))
        r3.add(backspaceKey(1.5f))
        root.addView(rowOf(r3))

        root.addView(
            rowOf(
                listOf(
                    key("?123", 1.3f, Kind.FUNC, 15f) { go(Page.NUMBERS) },
                    key("☺", 1.1f, Kind.FUNC, 20f) { go(Page.EMOJI) },
                    key(",", 1f) { typeKey(",") },
                    key("◂  English  ▸", 4f, Kind.KEY, 14f, skin.hint) { pressSpace() },
                    key(".", 1f) { typeKey(".") },
                    enterKey(1.4f)
                )
            )
        )
    }

    private fun buildNumbers() {
        root.addView(rowOf(plainKeys("1234567890")))
        root.addView(rowOf(plainKeys("+-×÷=()/\\^")))
        root.addView(rowOf(plainKeys("<>≤≥≠±°%_!")))
        root.addView(
            rowOf(
                listOf(
                    key("ABC", 1.3f, Kind.FUNC, 15f) { go(Page.LETTERS) },
                    key("∑π", 1.3f, Kind.FUNC, 17f) { go(Page.SYMBOLS) },
                    key(",", 1f) { typeKey(",") },
                    key("space", 3f, Kind.KEY, 13f, skin.hint) { pressSpace() },
                    key(".", 1f) { typeKey(".") },
                    key("?", 1f) { typeKey("?") },
                    backspaceKey(1.3f),
                    enterKey(1.3f)
                )
            )
        )
    }

    private fun buildSymbols() {
        val cats = SymbolData.categories
        root.addView(tabsRow(cats.map { it.name }, category) { i ->
            category = i
            render()
        })

        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        cats[category].items.chunked(COLS).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { sym ->
                cells.add(key(sym.label, 1f, Kind.KEY, 20f) { commitSymbol(sym.insert) })
            }
            repeat(COLS - chunk.size) { cells.add(spacer(1f)) }
            grid.addView(rowOf(cells))
        }
        root.addView(scrollArea(grid))
        root.addView(bottomBar())
    }

    private fun buildEmoji() {
        val cats = EmojiData.categories
        root.addView(tabsRow(cats.map { it.icon }, emojiCat) { i ->
            emojiCat = i
            render()
        })

        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        cats[emojiCat].items.chunked(COLS).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { e ->
                cells.add(key(e, 1f, Kind.FLAT, 24f, skin.text) { commitSymbol(e) })
            }
            repeat(COLS - chunk.size) { cells.add(spacer(1f)) }
            grid.addView(rowOf(cells))
        }
        root.addView(scrollArea(grid))
        root.addView(bottomBar())
    }

    private fun buildClip() {
        root.addView(
            titleRow("Clipboard", if (clips.isEmpty()) null else "Clear") {
                clips.clear()
                render()
            }
        )
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        if (clips.isEmpty()) {
            list.addView(
                TextView(this).apply {
                    text = "Nothing copied yet.\nCopy some text and it will show up here."
                    setTextColor(skin.hint)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    gravity = Gravity.CENTER
                    setPadding(dp(8), dp(24), dp(8), dp(8))
                }
            )
        } else {
            clips.forEach { c ->
                list.addView(listKey(c) { commit(c) })
            }
        }
        root.addView(scrollArea(list))
        root.addView(bottomBar())
    }

    private fun buildEdit() {
        root.addView(titleRow("Text editing", null) { })
        val ic = { id: Int -> currentInputConnection?.performContextMenuAction(id); Unit }
        root.addView(
            rowOf(
                listOf(
                    key("Select all", 1f, Kind.KEY, 13f) { ic(android.R.id.selectAll) },
                    key("Cut", 1f, Kind.KEY, 13f) { ic(android.R.id.cut) },
                    key("Copy", 1f, Kind.KEY, 13f) { ic(android.R.id.copy) },
                    key("Paste", 1f, Kind.KEY, 13f) { ic(android.R.id.paste) }
                )
            )
        )
        root.addView(
            rowOf(
                listOf(
                    key("Home", 1f, Kind.KEY, 13f) { pressKeyCode(KeyEvent.KEYCODE_MOVE_HOME) },
                    key("▲", 1f, Kind.KEY, 18f) { pressKeyCode(KeyEvent.KEYCODE_DPAD_UP) },
                    key("End", 1f, Kind.KEY, 13f) { pressKeyCode(KeyEvent.KEYCODE_MOVE_END) },
                    key("Select", 1f, if (selectMode) Kind.ACCENT else Kind.FUNC, 13f) {
                        selectMode = !selectMode
                        render()
                    }
                )
            )
        )
        root.addView(
            rowOf(
                listOf(
                    key("◀", 1f, Kind.KEY, 18f) { pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT) },
                    key("▼", 1f, Kind.KEY, 18f) { pressKeyCode(KeyEvent.KEYCODE_DPAD_DOWN) },
                    key("▶", 1f, Kind.KEY, 18f) { pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT) },
                    backspaceKey(1f)
                )
            )
        )
        root.addView(bottomBar())
    }

    private fun buildNumpad() {
        root.addView(titleRow("Number pad", null) { })
        root.addView(rowOf(plainKeys("789÷")))
        root.addView(rowOf(plainKeys("456×")))
        root.addView(rowOf(plainKeys("123−")))
        root.addView(
            rowOf(
                listOf(
                    key("ABC", 1.3f, Kind.FUNC, 15f) { go(Page.LETTERS) },
                    key(",", 1f) { typeKey(",") },
                    key("0", 1.6f) { typeKey("0") },
                    key(".", 1f) { typeKey(".") },
                    key("+", 1f) { typeKey("+") },
                    key("=", 1f) { typeKey("=") },
                    backspaceKey(1.2f),
                    enterKey(1.3f)
                )
            )
        )
    }

    private fun buildThemePage() {
        root.addView(titleRow("Theme & font", null) { })
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        Themes.all.chunked(3).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { t -> cells.add(themeSwatch(t)) }
            repeat(3 - chunk.size) { cells.add(spacer(1f)) }
            col.addView(rowOf(cells))
        }
        Fonts.all.chunked(3).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { f ->
                val selected = f.id == prefs.fontId
                val k = key(f.name, 1f, if (selected) Kind.ACCENT else Kind.FUNC, 15f) { chooseFont(f.id) }
                (k.getChildAt(0) as TextView).typeface = Fonts.typeface(f.id)
                cells.add(k)
            }
            repeat(3 - chunk.size) { cells.add(spacer(1f)) }
            col.addView(rowOf(cells))
        }
        col.addView(
            rowOf(
                listOf(
                    key("My photo: choose in app", 1f, Kind.FUNC, 14f) {
                        val i = Intent(this, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    }
                )
            )
        )
        root.addView(scrollArea(col))
        root.addView(bottomBar())
    }

    private fun themeSwatch(t: KeyTheme): View {
        val selected = t.id == prefs.themeId
        val f = FrameLayout(this)
        f.isClickable = true
        val shape = GradientDrawable()
        shape.cornerRadius = dp(6).toFloat()
        shape.setColor(if (t.key != 0) t.key else t.bg)
        shape.setStroke(dp(if (selected) 3 else 1), if (selected) t.accent else Color.GRAY)
        f.background = shape
        f.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            .apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
        val tv = TextView(this)
        tv.text = t.name
        tv.gravity = Gravity.CENTER
        tv.setTextColor(t.text)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        f.addView(tv, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        f.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            chooseTheme(t.id)
        }
        return f
    }

    // ---------------------------------------------------------------- top toolbar

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
        val h = strip ?: return
        h.removeAllViews()
        h.addView(buildToolbar())
    }

    private fun buildToolbar(): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        row.addView(fixedKey("⌨", false) { switchKeyboard() })
        row.addView(toolbarMiddle())
        row.addView(fixedKey("⋯", toolsOpen) {
            toolsOpen = !toolsOpen
            fillStrip()
        })
        row.addView(fixedKey("🎤", listening) { toggleVoice() })
        return row
    }

    private fun toolbarMiddle(): View {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.HORIZONTAL
        box.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        when {
            toolsOpen -> box.addView(toolScroll())
            page == Page.LETTERS && suggestions.isNotEmpty() -> {
                suggestions.forEachIndexed { i, w ->
                    if (i > 0) box.addView(divider())
                    box.addView(key(w, 1f, Kind.FLAT, 17f, skin.text) { acceptSuggestion(w) })
                }
                repeat(3 - suggestions.size) { box.addView(spacer(1f)) }
            }
            else -> box.addView(quickScroll())
        }
        return box
    }

    private fun toolScroll(): View {
        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.HORIZONTAL
        inner.addView(fixedKey("☺", false) { go(Page.EMOJI) })
        inner.addView(fixedKey("📋", false) { go(Page.CLIP) })
        inner.addView(fixedKey("✎", false) { go(Page.EDIT) })
        inner.addView(fixedKey("123", false) { go(Page.NUMPAD) })
        inner.addView(fixedKey("∑π", false) { go(Page.SYMBOLS) })
        inner.addView(fixedKey("x²", script == Script.SUP) { toggleScript(Script.SUP) })
        inner.addView(fixedKey("x₂", script == Script.SUB) { toggleScript(Script.SUB) })
        inner.addView(fixedKey("◐", false) { go(Page.THEME) })
        return hScroll(inner)
    }

    private fun quickScroll(): View {
        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.HORIZONTAL
        inner.addView(fixedKey("x²", script == Script.SUP) { toggleScript(Script.SUP) })
        inner.addView(fixedKey("x₂", script == Script.SUB) { toggleScript(Script.SUB) })
        QUICK.forEach { s -> inner.addView(fixedKey(s, false, skin.text) { commitSymbol(s) }) }
        return hScroll(inner)
    }

    private fun hScroll(inner: View): View =
        HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            addView(inner)
        }

    private fun divider(): View {
        val c = skin.hint
        return View(this).apply {
            setBackgroundColor(Color.argb(90, Color.red(c), Color.green(c), Color.blue(c)))
            layoutParams = LinearLayout.LayoutParams(dp(1), dp(22)).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

    /** টুলবারের নির্দিষ্ট-চওড়া আইকন বাটন। */
    private fun fixedKey(
        label: String,
        active: Boolean,
        textColor: Int = skin.funcText,
        onTap: () -> Unit
    ): View {
        val v = key(label, 1f, if (active) Kind.ACCENT else Kind.FLAT, 20f, if (active) Color.WHITE else textColor, onTap = onTap)
        v.layoutParams = LinearLayout.LayoutParams(dp(44), ViewGroup.LayoutParams.MATCH_PARENT)
            .apply { setMargins(dp(1), dp(2), dp(1), dp(2)) }
        return v
    }

    // ---------------------------------------------------------------- generic UI pieces

    private fun bgFor(kind: Kind): Int = when (kind) {
        Kind.KEY -> skin.key
        Kind.FUNC -> skin.func
        Kind.FLAT -> 0
        Kind.ACCENT -> skin.accent
    }

    private fun fgFor(kind: Kind): Int = when (kind) {
        Kind.KEY -> skin.text
        Kind.FUNC, Kind.FLAT -> skin.funcText
        Kind.ACCENT -> Color.WHITE
    }

    private fun letterKeys(chars: String, hints: String, upper: Boolean): List<View> =
        chars.mapIndexed { i, ch ->
            val t = if (upper) ch.uppercaseChar().toString() else ch.toString()
            val h = hints[i].toString()
            key(t, 1f, Kind.KEY, 22f, hint = h, onLong = { typeKey(h) }) { typeKey(t) }
        }

    private fun plainKeys(chars: String): List<View> =
        chars.map { ch -> key(ch.toString(), 1f, Kind.KEY, 20f) { typeKey(ch.toString()) } }

    private fun shiftKey(): View {
        val active = shift || caps
        return key(if (caps) "⇪" else "⇧", 1.5f, if (active) Kind.ACCENT else Kind.FUNC, 20f) {
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

    private fun enterKey(weight: Float): View =
        key("↵", weight, Kind.FUNC, 22f) { pressEnter() }

    private fun backspaceKey(weight: Float): View {
        val v = frameKey("⌫", weight, Kind.FUNC, 20f, fgFor(Kind.FUNC), null)
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
        kind: Kind = Kind.KEY,
        textSp: Float = 20f,
        textColor: Int = fgFor(kind),
        hint: String? = null,
        onLong: (() -> Unit)? = null,
        onTap: () -> Unit
    ): FrameLayout {
        val v = frameKey(label, weight, kind, textSp, textColor, hint)
        v.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onTap()
        }
        if (onLong != null) {
            v.setOnLongClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onLong()
                true
            }
        }
        return v
    }

    private fun frameKey(
        label: String,
        weight: Float,
        kind: Kind,
        textSp: Float,
        textColor: Int,
        hint: String?
    ): FrameLayout {
        val f = FrameLayout(this)
        f.isClickable = true
        f.background = keyBackground(bgFor(kind))
        f.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
            .apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }

        val tv = TextView(this)
        tv.text = label
        tv.gravity = Gravity.CENTER
        tv.isSingleLine = true
        tv.setTextColor(textColor)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSp)
        tv.typeface = font
        f.addView(
            tv,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        if (hint != null) {
            val h = TextView(this)
            h.text = hint
            h.gravity = Gravity.CENTER_HORIZONTAL
            h.setTextColor(skin.hint)
            h.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            h.typeface = font
            h.setPadding(0, dp(1), 0, 0)
            f.addView(
                h,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
                )
            )
        }
        return f
    }

    private fun keyBackground(color: Int) = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), roundRect(skin.pressed))
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

    /** ছোট পেজগুলোর (সিম্বল/ইমোজি) ক্যাটাগরি ট্যাব। */
    private fun tabsRow(labels: List<String>, selected: Int, onSelect: (Int) -> Unit): View {
        val tabs = LinearLayout(this)
        tabs.orientation = LinearLayout.HORIZONTAL
        labels.forEachIndexed { i, name ->
            val tab = key(name, 1f, if (i == selected) Kind.ACCENT else Kind.FUNC, 15f, if (i == selected) Color.WHITE else skin.text) {
                onSelect(i)
            }
            tab.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
            tab.minimumWidth = dp(44)
            tab.setPadding(dp(12), 0, dp(12), 0)
            tabs.addView(tab)
        }
        return HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(STRIP_H)
            )
            addView(tabs)
        }
    }

    /** শিরোনামের সারি (বাঁয়ে নাম, ডানে ঐচ্ছিক বাটন)। */
    private fun titleRow(title: String, actionLabel: String?, action: () -> Unit): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(STRIP_H)
        )
        val tv = TextView(this)
        tv.text = title
        tv.setTextColor(skin.text)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        tv.typeface = font
        tv.gravity = Gravity.CENTER_VERTICAL
        tv.setPadding(dp(10), 0, 0, 0)
        row.addView(tv, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        if (actionLabel != null) {
            val b = key(actionLabel, 1f, Kind.FUNC, 14f) { action() }
            b.layoutParams = LinearLayout.LayoutParams(dp(80), ViewGroup.LayoutParams.MATCH_PARENT)
                .apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
            row.addView(b)
        }
        return row
    }

    private fun scrollArea(content: View): View =
        ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(ROW_H * 3)
            )
            addView(content)
        }

    private fun listKey(text: String, onTap: () -> Unit): View {
        val f = FrameLayout(this)
        f.isClickable = true
        f.background = keyBackground(if (skin.key != 0) skin.key else skin.func)
        f.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(ROW_H - 8)
        ).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) }
        val tv = TextView(this)
        tv.text = text.replace('\n', ' ')
        tv.setTextColor(skin.text)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        tv.typeface = font
        tv.isSingleLine = true
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.gravity = Gravity.CENTER_VERTICAL
        tv.setPadding(dp(12), 0, dp(12), 0)
        f.addView(
            tv,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        f.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onTap()
        }
        return f
    }

    /** সিম্বল/ইমোজি/ক্লিপ/এডিট/থিম পেজের নিচের সারি। */
    private fun bottomBar(): View =
        rowOf(
            listOf(
                key("ABC", 1.5f, Kind.FUNC, 15f) { go(Page.LETTERS) },
                key("?123", 1.5f, Kind.FUNC, 15f) { go(Page.NUMBERS) },
                key("space", 4f, Kind.KEY, 13f, skin.hint) { pressSpace() },
                backspaceKey(1.5f),
                enterKey(1.5f)
            )
        )

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // ---------------------------------------------------------------- voice typing

    private fun toggleVoice() {
        if (listening) {
            recognizer?.stopListening()
            listening = false
            fillStrip()
            return
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val i = Intent(this, MicPermissionActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            return
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            toast("Voice typing is not available on this phone")
            return
        }
        startVoice()
    }

    private fun startVoice() {
        val r = recognizer ?: SpeechRecognizer.createSpeechRecognizer(this).also { recognizer = it }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onError(error: Int) {
                listening = false
                fillStrip()
                if (error != SpeechRecognizer.ERROR_NO_MATCH &&
                    error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT &&
                    error != SpeechRecognizer.ERROR_CLIENT
                ) {
                    toast("Voice typing error ($error)")
                }
            }

            override fun onResults(results: Bundle?) {
                listening = false
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    currentInputConnection?.commitText("$text ", 1)
                }
                fillStrip()
                updateSuggestions()
            }
        })
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.voiceLang)
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        listening = true
        fillStrip()
        r.startListening(i)
    }

    // ---------------------------------------------------------------- clipboard

    private fun onClipChanged() {
        try {
            val clip = clipboard?.primaryClip ?: return
            if (clip.itemCount == 0) return
            val t = clip.getItemAt(0).coerceToText(this)?.toString()?.trim() ?: return
            if (t.isEmpty() || t.length > 2000) return
            clips.remove(t)
            clips.add(0, t)
            while (clips.size > 20) clips.removeAt(clips.size - 1)
        } catch (e: Exception) {
            // ক্লিপবোর্ড পড়া না গেলে চুপচাপ বাদ দিই
        }
    }

    private fun switchKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
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

    private fun textBefore(n: Int): String =
        currentInputConnection?.getTextBeforeCursor(n, 0)?.toString() ?: ""

    /** কার্সরের আগের অসম্পূর্ণ শব্দের শুরুর index। */
    private fun prefixStart(before: String): Int {
        var i = before.length
        while (i > 0 && before[i - 1].isLetter()) i--
        return i
    }

    /** [text]-এর শেষ শব্দ (শুধু যদি তার পরে স্পেস থাকে)। */
    private fun previousWord(text: String): String? {
        if (text.isEmpty() || !text[text.length - 1].isWhitespace()) return null
        var end = text.length
        while (end > 0 && text[end - 1].isWhitespace()) end--
        var start = end
        while (start > 0 && text[start - 1].isLetter()) start--
        return if (end > start) text.substring(start, end) else null
    }

    private fun atSentenceStart(text: String): Boolean {
        val t = text.trimEnd(' ')
        if (t.isEmpty()) return true
        val c = t[t.length - 1]
        return c == '.' || c == '!' || c == '?' || c == '\n' || c == '।'
    }

    private fun capLike(word: String, prefix: String): String = when {
        prefix.length > 1 && prefix.all { it.isUpperCase() } -> word.uppercase()
        prefix.isNotEmpty() && prefix[0].isUpperCase() ->
            word.replaceFirstChar { it.uppercaseChar() }
        word == "i" -> "I"
        else -> word
    }

    private fun learnCurrent() {
        if (!learning || script != Script.NONE) return
        val before = textBefore(48)
        val i = prefixStart(before)
        val w = before.substring(i)
        if (w.length >= 2) store.learn(w, previousWord(before.substring(0, i)))
    }

    private fun updateSuggestions() {
        val next: List<String> =
            if (!learning || script != Script.NONE || page != Page.LETTERS) {
                emptyList()
            } else {
                val before = textBefore(48)
                val i = prefixStart(before)
                val prefix = before.substring(i)
                val head = before.substring(0, i)
                if (prefix.isEmpty()) {
                    val cap = atSentenceStart(head)
                    store.nextWords(previousWord(head), 3)
                        .map { if (cap) capLike(it, "A") else capLike(it, "") }
                } else {
                    store.suggest(prefix, 3).map { capLike(it, prefix) }
                }
            }
        if (next != suggestions) {
            suggestions = next
            fillStrip()
        }
    }

    private fun acceptSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        val before = textBefore(48)
        val i = prefixStart(before)
        val prefix = before.substring(i)
        val prev = previousWord(before.substring(0, i))
        ic.beginBatchEdit()
        if (prefix.isNotEmpty()) ic.deleteSurroundingText(prefix.length, 0)
        ic.commitText("$word ", 1)
        ic.endBatchEdit()
        if (learning) store.learn(word, prev)
        updateSuggestions()
    }

    // ---------------------------------------------------------------- input actions

    private fun commit(s: String) {
        currentInputConnection?.commitText(s, 1)
    }

    /** চিহ্ন বসানোর আগে চলতি শব্দটা শিখে নেয়। */
    private fun commitSymbol(s: String) {
        learnCurrent()
        commit(s)
        updateSuggestions()
    }

    private fun toggleScript(s: Script) {
        script = if (script == s) Script.NONE else s
        render()
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

    private fun pressKeyCode(code: Int) {
        val ic = currentInputConnection ?: return
        val meta = if (selectMode) KeyEvent.META_SHIFT_ON else 0
        val now = SystemClock.uptimeMillis()
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0, meta))
        ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0, meta))
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
        val before = ic.getTextBeforeCursor(1, 0)
        if (before.isNullOrEmpty()) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
        } else {
            ic.deleteSurroundingTextInCodePoints(1, 0)
        }
    }

    // ---------------------------------------------------------------- constants

    private companion object {
        const val ROW_H = 52      // dp: প্রতিটি সারির উচ্চতা (মার্জিনসহ)
        const val STRIP_H = 42    // dp: উপরের টুলবার
        const val COLS = 8        // সিম্বল/ইমোজি পেজের কলাম

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
