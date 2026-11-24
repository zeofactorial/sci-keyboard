package com.example.scikeyboard

import android.Manifest
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import java.io.File
import kotlin.math.abs
import kotlin.math.hypot

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
 * অক্ষরের সারিগুলোর কনটেইনার: আঙুল টেনে (swipe) শব্দ লেখার জন্য।
 * ছোট টাচ হলে আগের মতোই কী চাপা হয়; আঙুল বেশি সরলে swipe শুরু হয়।
 * কী-এর tag-এ অক্ষর (Char) থাকলে সেটাই swipe-এর কী।
 */
private class SwipeLayout(
    ctx: Context,
    private val enabled: () -> Boolean,
    private val slopPx: Int,
    private val trailPx: Float,
    private val trailColor: Int,
    private val onSwipe: (List<Char>, List<Char>) -> Unit
) : LinearLayout(ctx) {

    private val pts = ArrayList<FloatArray>()
    private var swiping = false
    private var eligible = false
    private var startX = 0f
    private var startY = 0f
    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        orientation = LinearLayout.VERTICAL
        setWillNotDraw(false)
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = trailPx
        paint.color = trailColor
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (swiping && pts.size > 1) {
            path.reset()
            path.moveTo(pts[0][0], pts[0][1])
            for (i in 1 until pts.size) path.lineTo(pts[i][0], pts[i][1])
            canvas.drawPath(path, paint)
        }
    }

    /** (x, y) বিন্দুর সবচেয়ে কাছের অক্ষর-কী। */
    private fun keyAt(x: Float, y: Float): Char? {
        if (childCount == 0) return null
        val top = getChildAt(0).top.toFloat()
        val bottom = getChildAt(childCount - 1).bottom.toFloat() - 1f
        val yy = y.coerceIn(top, bottom)
        for (r in 0 until childCount) {
            val row = getChildAt(r) as? ViewGroup ?: continue
            if (yy < row.top || yy > row.bottom) continue
            var best: Char? = null
            var bestD = Float.MAX_VALUE
            for (k in 0 until row.childCount) {
                val key = row.getChildAt(k)
                val c = key.tag as? Char ?: continue
                val cx = row.left + (key.left + key.right) / 2f
                val d = abs(x - cx)
                if (d < bestD) {
                    bestD = d
                    best = c
                }
            }
            return best
        }
        return null
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                swiping = false
                pts.clear()
                eligible = enabled() && keyAt(ev.x, ev.y) != null
                if (eligible) pts.add(floatArrayOf(ev.x, ev.y))
            }
            MotionEvent.ACTION_MOVE -> {
                if (eligible && !swiping) {
                    pts.add(floatArrayOf(ev.x, ev.y))
                    if (hypot(ev.x - startX, ev.y - startY) > slopPx) {
                        swiping = true
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> reset()
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> return eligible
            MotionEvent.ACTION_MOVE -> {
                if (!eligible) return false
                pts.add(floatArrayOf(ev.x, ev.y))
                if (!swiping && hypot(ev.x - startX, ev.y - startY) > slopPx) swiping = true
                if (swiping) invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (swiping) {
                    pts.add(floatArrayOf(ev.x, ev.y))
                    finishSwipe()
                }
                reset()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                reset()
                return true
            }
        }
        return false
    }

    private fun reset() {
        val wasSwiping = swiping
        swiping = false
        eligible = false
        pts.clear()
        if (wasSwiping) invalidate()
    }

    private fun finishSwipe() {
        if (pts.size < 2) return
        val step = maxOf(slopPx / 2f, 6f)

        // পথটাকে সমান দূরত্বের বিন্দুতে ভাগ করে প্রতিটার কী বের করি
        val keys = ArrayList<Char>()
        fun addKey(x: Float, y: Float) {
            val c = keyAt(x, y) ?: return
            if (keys.isEmpty() || keys[keys.size - 1] != c) keys.add(c)
        }
        addKey(pts[0][0], pts[0][1])
        for (i in 1 until pts.size) {
            val a = pts[i - 1]
            val b = pts[i]
            val d = hypot(b[0] - a[0], b[1] - a[1])
            val n = (d / step).toInt()
            for (j in 1..n) {
                val t = j * step / d
                addKey(a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t)
            }
            addKey(b[0], b[1])
        }

        // মোড় নেওয়া বিন্দুগুলো (এই অক্ষরগুলো শব্দে অবশ্যই থাকবে)
        val spacing = slopPx * 1.4f
        val kept = ArrayList<FloatArray>()
        kept.add(pts[0])
        for (i in 1 until pts.size) {
            val last = kept[kept.size - 1]
            if (hypot(pts[i][0] - last[0], pts[i][1] - last[1]) >= spacing) kept.add(pts[i])
        }
        val end = pts[pts.size - 1]
        val lastKept = kept[kept.size - 1]
        if (lastKept[0] != end[0] || lastKept[1] != end[1]) kept.add(end)

        val corners = ArrayList<Char>()
        fun addCorner(x: Float, y: Float) {
            val c = keyAt(x, y) ?: return
            if (corners.isEmpty() || corners[corners.size - 1] != c) corners.add(c)
        }
        addCorner(kept[0][0], kept[0][1])
        for (i in 1 until kept.size - 1) {
            val v1x = kept[i][0] - kept[i - 1][0]
            val v1y = kept[i][1] - kept[i - 1][1]
            val v2x = kept[i + 1][0] - kept[i][0]
            val v2y = kept[i + 1][1] - kept[i][1]
            val l1 = hypot(v1x, v1y)
            val l2 = hypot(v2x, v2y)
            if (l1 == 0f || l2 == 0f) continue
            val cos = (v1x * v2x + v1y * v2y) / (l1 * l2)
            if (cos < 0.5f) addCorner(kept[i][0], kept[i][1])
        }
        addCorner(kept[kept.size - 1][0], kept[kept.size - 1][1])

        if (keys.size >= 2) onSwipe(keys, corners)
    }
}

/**
 * Scientific Keyboard: QWERTY + গাণিতিক চিহ্ন + শব্দ সাজেশন + ইমোজি + ক্লিপবোর্ড
 * + টেক্সট এডিট + ভয়েস টাইপিং + থিম। UI পুরোটাই কোডে তৈরি।
 */
class SciKeyboardService : InputMethodService() {

    private enum class Page { LETTERS, NUMBERS, SYMBOLS, EMOJI, SEARCH, CLIP, EDIT, NUMPAD, THEME, BANGLA, STICKER, DIGITS }
    private enum class Script { NONE, SUP, SUB }
    private enum class Kind { KEY, FUNC, FLAT, ACCENT }

    private var page = Page.LETTERS
    private var shift = false
    private var caps = false
    private var lastShiftTap = 0L
    private var script = Script.NONE
    private var category = 0
    private var emojiCat = 2          // 0 = সাম্প্রতিক, 1 = সার্চ, 2+ = ক্যাটাগরি
    private var bnLayer = 0
    private var emojiQuery = ""
    private var variantFor: String? = null
    private var variantList: List<String> = emptyList()

    // অটোকারেক্ট (আসল শব্দ, যা বসেছে), সোয়াইপের বিকল্প শব্দ
    private var lastCorrection: Pair<String, String>? = null
    private var swipeAlt: List<String>? = null
    private var swipeText = ""

    // শর্টকাট (text expander) ক্যাশ
    private var shortcutRaw = ""
    private var shortcutCache: Map<String, String> = emptyMap()

    // স্টিকারের থাম্বনেইল ক্যাশ
    private val thumbs = HashMap<String, Bitmap>()
    private var toolsOpen = false
    private var selectMode = false
    private val bangla = BanglaInput()

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
    private var listening = false          // true = ব্যবহারকারী নিজে বন্ধ না করা পর্যন্ত শুনতে থাকবে
    private var voiceFails = 0
    private val voiceRestart = Runnable { if (listening) startVoice() }

    // অ্যাপের সেটিং স্ক্রিনে কিছু বদলালে কীবোর্ড সাথে সাথে নতুন করে সাজে
    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key in LIVE_KEYS) onPrefChanged()
    }

    private fun onPrefChanged() {
        if (!::root.isInitialized) return
        reloadSkin()
        applyBackground()
        render()
        updateSuggestions()
    }

    // টপ মেনুর নির্বাচিত বাটন সামনে আনা, সেটিংস পেজের স্ক্রল ধরে রাখা
    private var toolSel = -1
    private var settingsY = 0

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
        prefs.listen(prefListener)
        reloadSkin()
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.addPrimaryClipChangedListener(clipListener)
    }

    override fun onDestroy() {
        prefs.unlisten(prefListener)
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
        reloadSkin()   // অ্যাপে সেটিং বদলালে সাথে সাথে কীবোর্ডে কাজ করবে
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
            variantFor = null
            lastCorrection = null
            swipeAlt = null
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
        settingsY = 0
        variantFor = null
        swipeAlt = null
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
            Page.SEARCH -> buildSearch()
            Page.BANGLA -> {
                root.addView(newStrip())
                buildBangla()
            }
            Page.STICKER -> buildSticker()
            Page.CLIP -> buildClip()
            Page.EDIT -> buildEdit()
            Page.NUMPAD -> buildNumpad()
            Page.DIGITS -> buildDigits()
            Page.THEME -> buildThemePage()
        }
    }

    private fun buildLetters() {
        val up = shift || caps
        val tc = skin.accent
        val swipe = SwipeLayout(
            this,
            { prefs.swipe && script == Script.NONE && page == Page.LETTERS },
            dp(14),
            dp(5).toFloat(),
            Color.argb(170, Color.red(tc), Color.green(tc), Color.blue(tc)),
            { keys, corners -> onSwipeWord(keys, corners) }
        )
        swipe.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        swipe.addView(rowOf(letterKeys("qwertyuiop", "1234567890", up)))
        swipe.addView(
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
        swipe.addView(rowOf(r3))
        root.addView(swipe)

        root.addView(
            rowOf(
                listOf(
                    key("?123", 1.3f, Kind.FUNC, 15f) { go(Page.NUMBERS) },
                    key("☺", 1.1f, Kind.FUNC, 20f) { go(Page.EMOJI) },
                    key(",", 1f) { typeKey(",") },
                    key("বাং", 1.2f, Kind.FUNC, 14f) { go(Page.BANGLA) },
                    key(
                        if (bangla.enabled) "◂  বাংলা  ▸" else "◂  English  ▸",
                        3f, Kind.KEY, 14f, skin.hint,
                        onLong = {
                            bangla.toggle(currentInputConnection)
                            render()
                            updateSuggestions()
                        }
                    ) { pressSpace() },
                    key(".", 1f) { typeKey(".") },
                    enterKey(1.4f)
                )
            )
        )
    }

    /** বাংলা কীবোর্ড: ৩টা স্তর (ব্যঞ্জন / স্বর-কার / সংখ্যা-যতিচিহ্ন)। */
    private fun buildBangla() {
        val n = BanglaData.layers.size
        val layer = BanglaData.layers[bnLayer.coerceIn(0, n - 1)]
        layer.rows.forEach { r ->
            root.addView(
                rowOf(
                    r.map { sym ->
                        key(BanglaData.display(sym), 1f, Kind.KEY, 20f) { commit(BanglaData.insert(sym)) }
                    }
                )
            )
        }
        val next = BanglaData.layers[(bnLayer + 1) % n].label
        root.addView(
            rowOf(
                listOf(
                    key("ABC", 1.3f, Kind.FUNC, 15f) { go(Page.LETTERS) },
                    key(next, 1.3f, Kind.FUNC, 16f) {
                        bnLayer = (bnLayer + 1) % n
                        render()
                    },
                    key("বাংলা", 3f, Kind.KEY, 13f, skin.hint) { commit(" ") },
                    key("।", 1f) { commit("।") },
                    backspaceKey(1.3f),
                    enterKey(1.3f)
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
                cells.add(key(sym.label, 1f, Kind.KEY, if (sym.label.length > 3) 14f else 20f) { commitSym(sym) })
            }
            repeat(COLS - chunk.size) { cells.add(spacer(1f)) }
            grid.addView(rowOf(cells))
        }
        root.addView(scrollArea(grid))
        root.addView(bottomBar())
    }

    private fun buildEmoji() {
        val cats = EmojiData.categories
        val labels = listOf("🕘", "🔍") + cats.map { it.icon }
        val vf = variantFor
        if (vf != null) {
            root.addView(variantRow(vf))
        } else {
            root.addView(tabsRow(labels, emojiCat) { i ->
                if (i == 1) {
                    emojiQuery = ""
                    go(Page.SEARCH)
                } else {
                    emojiCat = i
                    render()
                }
            })
        }

        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val items: List<String>
        val cols: Int
        val size: Float
        if (emojiCat == 0) {
            items = prefs.recentEmoji.filter { EmojiData.supported(it) }
            cols = COLS
            size = 24f
        } else {
            val cat = cats[(emojiCat - 2).coerceIn(0, cats.size - 1)]
            items = cat.items()
            cols = cat.cols
            size = cat.textSize
        }
        if (items.isEmpty()) {
            grid.addView(
                TextView(this).apply {
                    text = "No recent emoji yet.\nEmoji you use will show up here."
                    setTextColor(skin.hint)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    gravity = Gravity.CENTER
                    setPadding(dp(8), dp(24), dp(8), dp(8))
                }
            )
        }
        items.chunked(cols).forEach { chunk ->
            val cells = ArrayList<View>()
            chunk.forEach { e ->
                cells.add(
                    key(e, 1f, Kind.FLAT, size, skin.text, onLong = { showVariants(e) }) {
                        commitEmoji(e)
                    }
                )
            }
            repeat(cols - chunk.size) { cells.add(spacer(1f)) }
            grid.addView(rowOf(cells))
        }
        root.addView(scrollArea(grid))
        root.addView(bottomBar())
    }

    /** ইমোজি চেপে ধরলে skin tone-এর সারি। */
    private fun showVariants(e: String) {
        val v = EmojiData.skinVariants(e) ?: return
        variantFor = e
        variantList = v
        render()
    }

    private fun variantRow(base: String): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(STRIP_H)
        )
        (listOf(base) + variantList).forEach { v ->
            row.addView(
                key(v, 1f, Kind.KEY, 22f) {
                    variantFor = null
                    commitEmoji(v)
                    render()
                }
            )
        }
        row.addView(
            key("✕", 1f, Kind.FUNC, 16f) {
                variantFor = null
                render()
            }
        )
        return row
    }

    private fun commitEmoji(e: String) {
        prefs.addRecentEmoji(e)
        commitSymbol(e)
    }

    /** ইমোজি সার্চ: উপরে লেখা + ফলাফল, নিচে ছোট QWERTY। */
    private fun buildSearch() {
        val results = EmojiNames.search(emojiQuery, 40)

        val top = LinearLayout(this)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(rowH())
        )
        val q = TextView(this)
        q.text = if (emojiQuery.isEmpty()) "🔍 heart, hasi…" else "🔍 $emojiQuery"
        q.setTextColor(if (emojiQuery.isEmpty()) skin.hint else skin.text)
        q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        q.typeface = font
        q.gravity = Gravity.CENTER_VERTICAL
        q.isSingleLine = true
        q.ellipsize = TextUtils.TruncateAt.START
        q.setPadding(dp(8), 0, dp(4), 0)
        top.addView(q, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.3f))

        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.HORIZONTAL
        results.forEach { e -> inner.addView(fixedKey(e, false, skin.text) { commitEmoji(e) }) }
        val sc = HorizontalScrollView(this)
        sc.isHorizontalScrollBarEnabled = false
        sc.addView(inner)
        top.addView(sc, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2.7f))
        root.addView(top)

        root.addView(rowOf(searchKeys("qwertyuiop")))
        root.addView(rowOf(listOf(spacer(0.5f)) + searchKeys("asdfghjkl") + listOf(spacer(0.5f))))
        root.addView(
            rowOf(
                listOf(spacer(1.5f)) + searchKeys("zxcvbnm") +
                    listOf(
                        key("⌫", 1.5f, Kind.FUNC, 20f) {
                            emojiQuery = emojiQuery.dropLast(1)
                            render()
                        }
                    )
            )
        )
        root.addView(
            rowOf(
                listOf(
                    key("☺", 1.3f, Kind.FUNC, 20f) { go(Page.EMOJI) },
                    key("space", 4f, Kind.KEY, 13f, skin.hint) {
                        emojiQuery += " "
                        render()
                    },
                    key("✕", 1.3f, Kind.FUNC, 16f) {
                        emojiQuery = ""
                        render()
                    },
                    key("ABC", 1.3f, Kind.FUNC, 15f) { go(Page.LETTERS) }
                )
            )
        )
    }

    private fun searchKeys(chars: String): List<View> =
        chars.map { ch ->
            key(ch.toString(), 1f, Kind.KEY, 20f) {
                emojiQuery += ch
                render()
            }
        }

    // ---------------------------------------------------------------- stickers / GIF

    private fun openExtras() {
        val i = Intent(this, ExtrasActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    private fun buildSticker() {
        root.addView(titleRow("Stickers & GIFs", "Manage") { openExtras() })
        val files = prefs.stickerFiles()
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        if (files.isEmpty()) {
            col.addView(
                TextView(this).apply {
                    text = "No stickers yet.\nTap Manage to add your own images or GIFs."
                    setTextColor(skin.hint)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    gravity = Gravity.CENTER
                    setPadding(dp(8), dp(24), dp(8), dp(8))
                }
            )
        } else {
            files.chunked(4).forEach { chunk ->
                val r = LinearLayout(this)
                r.orientation = LinearLayout.HORIZONTAL
                r.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(rowH() + 24)
                )
                chunk.forEach { f -> r.addView(stickerCell(f)) }
                repeat(4 - chunk.size) { r.addView(spacer(1f)) }
                col.addView(r)
            }
        }
        root.addView(scrollArea(col))
        root.addView(bottomBar())
    }

    private fun stickerCell(f: File): View {
        val cell = FrameLayout(this)
        cell.isClickable = true
        cell.background = keyBackground(0)
        cell.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            .apply { setMargins(dp(3), dp(3), dp(3), dp(3)) }
        val iv = ImageView(this)
        iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
        thumb(f)?.let { iv.setImageBitmap(it) }
        cell.addView(
            iv,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        cell.setOnClickListener {
            tick(it)
            sendSticker(f)
        }
        return cell
    }

    private fun thumb(f: File): Bitmap? {
        thumbs[f.path]?.let { return it }
        return try {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeFile(f.path, o)
            var sample = 1
            while (o.outWidth / sample > 240 || o.outHeight / sample > 240) sample *= 2
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = sample
            val b = BitmapFactory.decodeFile(f.path, o2)
            if (b != null) {
                if (thumbs.size > 80) thumbs.clear()
                thumbs[f.path] = b
            }
            b
        } catch (e: Throwable) {
            null
        }
    }

    /** ছবি/GIF সরাসরি টেক্সট ফিল্ডে পাঠায় (যে অ্যাপ নেয় শুধু সেখানে)। */
    private fun sendSticker(f: File) {
        val ic = currentInputConnection
        val ei = currentInputEditorInfo
        if (ic == null || ei == null) return
        val mime = when (f.extension.lowercase()) {
            "gif" -> "image/gif"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }
        val accepted = EditorInfoCompat.getContentMimeTypes(ei)
        if (accepted.none { ClipDescription.compareMimeTypes(mime, it) }) {
            toast("This app does not accept stickers or GIFs")
            return
        }
        try {
            val uri = FileProvider.getUriForFile(this, "$packageName.stickers", f)
            val info = InputContentInfoCompat(uri, ClipDescription(f.name, arrayOf(mime)), null)
            var flags = 0
            if (Build.VERSION.SDK_INT >= 25) {
                flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
            }
            if (!InputConnectionCompat.commitContent(ic, ei, info, flags, null)) {
                toast("Could not send this sticker")
            }
        } catch (e: Throwable) {
            toast("Could not send this sticker")
        }
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
                    key("Copy", 1f, Kind.KEY, 13f) { copySelection() },
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

    /** শুধু বড় ০–৯ সংখ্যার পেজ (টপ মেনুর "0-9" বাটন থেকে খোলে)। */
    private fun buildDigits() {
        fun tall(items: List<View>): LinearLayout =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(rowH() * 5 / 4)
                )
                items.forEach { addView(it) }
            }
        root.addView(tall("12345".map { c -> key(c.toString(), 1f, Kind.KEY, 32f) { typeKey(c.toString()) } }))
        root.addView(tall("67890".map { c -> key(c.toString(), 1f, Kind.KEY, 32f) { typeKey(c.toString()) } }))
        root.addView(
            rowOf(
                listOf(
                    key("ABC", 1.5f, Kind.FUNC, 15f) { go(Page.LETTERS) },
                    key("space", 3f, Kind.KEY, 13f, skin.hint) { pressSpace() },
                    backspaceKey(1.5f),
                    enterKey(1.5f)
                )
            )
        )
    }

    private fun buildThemePage() {
        root.addView(titleRow("✦ Settings", null) { })
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val full = { LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) }

        // ---- থিম
        col.addView(sectionLabel("Theme"))
        Themes.all.chunked(3).forEach { chunk ->
            val r = LinearLayout(this)
            r.orientation = LinearLayout.HORIZONTAL
            r.layoutParams = full()
            chunk.forEach { t -> r.addView(themeCard(t)) }
            repeat(3 - chunk.size) { r.addView(spacer(1f)) }
            col.addView(r)
        }

        // ---- ফন্ট
        col.addView(sectionLabel("Font"))
        Fonts.all.chunked(3).forEach { chunk ->
            val r = LinearLayout(this)
            r.orientation = LinearLayout.HORIZONTAL
            r.layoutParams = full()
            chunk.forEach { f -> r.addView(fontChip(f)) }
            repeat(3 - chunk.size) { r.addView(spacer(1f)) }
            col.addView(r)
        }

        // ---- কীবোর্ড
        col.addView(sectionLabel("Keyboard"))
        val stepper = LinearLayout(this)
        stepper.orientation = LinearLayout.HORIZONTAL
        stepper.gravity = Gravity.CENTER_VERTICAL
        stepper.addView(roundBtn("−") {
            prefs.heightPct = prefs.heightPct - 10
            render()
        })
        stepper.addView(
            TextView(this).apply {
                text = "${prefs.heightPct}%"
                setTextColor(skin.text)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                typeface = font
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dp(52), ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        )
        stepper.addView(roundBtn("+") {
            prefs.heightPct = prefs.heightPct + 10
            render()
        })
        col.addView(settingRow("↕", "Keyboard height", "Make keys taller or shorter", stepper, null))
        col.addView(toggleRow("🔊", "Key sound", "Click sound on every key", prefs.sound) { prefs.sound = !prefs.sound })
        col.addView(toggleRow("📳", "Vibration", "Small buzz on every key", prefs.haptic) { prefs.haptic = !prefs.haptic })

        // ---- টাইপিং
        col.addView(sectionLabel("Typing"))
        col.addView(toggleRow("✨", "Autocorrect", "Fix one-letter typos on space", prefs.autocorrect) { prefs.autocorrect = !prefs.autocorrect })
        col.addView(toggleRow("🔊", "Volume keys move cursor", "Vol ▲ = left, Vol ▼ = right", prefs.volCursor) { prefs.volCursor = !prefs.volCursor })
        col.addView(toggleRow("👆", "Swipe typing", "Slide over letters to type a word", prefs.swipe) { prefs.swipe = !prefs.swipe })
        val voiceChip = TextView(this).apply {
            text = if (prefs.voiceLang == "bn-BD") "বাংলা" else "English"
            setTextColor(skin.accent)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = font
        }
        col.addView(
            settingRow("🎤", "Voice language", "Tap to switch. Long-press 🎤 works too", voiceChip) {
                prefs.voiceLang = if (prefs.voiceLang == "bn-BD") "en-US" else "bn-BD"
                render()
            }
        )

        // ---- আরও
        col.addView(sectionLabel("More"))
        col.addView(settingRow("🧩", "Shortcuts & stickers", "Text shortcuts, your own stickers and GIFs", chevron()) { openExtras() })
        col.addView(settingRow("🖼", "Photo theme & language", "Opens the app settings", chevron()) {
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        })

        val area = scrollArea(col) as ScrollView
        area.setOnScrollChangeListener { _, _, y, _, _ -> settingsY = y }
        area.post { area.scrollTo(0, settingsY) }
        root.addView(area)
        root.addView(bottomBar())
    }

    private fun cardColor(): Int = if (skin.func != 0) skin.func else Color.argb(48, 255, 255, 255)

    private fun sectionLabel(text: String): View {
        val tv = TextView(this)
        tv.text = text.uppercase()
        tv.setTextColor(skin.accent)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        tv.typeface = Typeface.create(font, Typeface.BOLD)
        tv.letterSpacing = 0.14f
        tv.setPadding(dp(10), dp(12), 0, dp(4))
        tv.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return tv
    }

    /** একটা সুন্দর কার্ড-সারি: বাঁয়ে আইকন, মাঝে নাম ও ছোট বর্ণনা, ডানে [trailing]। */
    private fun settingRow(icon: String, title: String, sub: String?, trailing: View?, onTap: (() -> Unit)?): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        val bg = GradientDrawable()
        bg.cornerRadius = dp(14).toFloat()
        bg.setColor(cardColor())
        row.background = bg
        row.setPadding(dp(12), 0, dp(12), 0)
        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(rowH() - 2)
        ).apply { setMargins(dp(4), dp(3), dp(4), dp(3)) }

        val ic = TextView(this)
        ic.text = icon
        ic.setTextColor(skin.text)
        ic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        ic.gravity = Gravity.CENTER
        row.addView(ic, LinearLayout.LayoutParams(dp(32), ViewGroup.LayoutParams.WRAP_CONTENT))

        val texts = LinearLayout(this)
        texts.orientation = LinearLayout.VERTICAL
        texts.gravity = Gravity.CENTER_VERTICAL
        val t = TextView(this)
        t.text = title
        t.setTextColor(skin.text)
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        t.typeface = font
        t.isSingleLine = true
        texts.addView(t)
        if (sub != null) {
            val st = TextView(this)
            st.text = sub
            st.setTextColor(skin.hint)
            st.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            st.isSingleLine = true
            st.ellipsize = TextUtils.TruncateAt.END
            texts.addView(st)
        }
        row.addView(
            texts,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                .apply { leftMargin = dp(8); rightMargin = dp(8) }
        )
        if (trailing != null) row.addView(trailing)
        if (onTap != null) {
            row.isClickable = true
            row.setOnClickListener {
                tick(it)
                onTap()
            }
        }
        return row
    }

    private fun toggleRow(icon: String, title: String, sub: String, on: Boolean, action: () -> Unit): View =
        settingRow(icon, title, sub, pillSwitch(on)) {
            action()
            render()
        }

    private fun pillSwitch(on: Boolean): View {
        val track = FrameLayout(this)
        val bg = GradientDrawable()
        bg.cornerRadius = dp(13).toFloat()
        bg.setColor(if (on) skin.accent else Color.argb(110, 150, 150, 150))
        track.background = bg
        val knob = View(this)
        val kb = GradientDrawable()
        kb.shape = GradientDrawable.OVAL
        kb.setColor(Color.WHITE)
        knob.background = kb
        val lp = FrameLayout.LayoutParams(
            dp(20), dp(20),
            (if (on) Gravity.END else Gravity.START) or Gravity.CENTER_VERTICAL
        )
        lp.setMargins(dp(3), 0, dp(3), 0)
        track.addView(knob, lp)
        track.layoutParams = LinearLayout.LayoutParams(dp(46), dp(26))
        return track
    }

    private fun roundBtn(label: String, onTap: () -> Unit): View {
        val tv = TextView(this)
        tv.text = label
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        val bg = GradientDrawable()
        bg.shape = GradientDrawable.OVAL
        bg.setColor(skin.accent)
        tv.background = bg
        tv.layoutParams = LinearLayout.LayoutParams(dp(30), dp(30))
        tv.isClickable = true
        tv.setOnClickListener {
            tick(it)
            onTap()
        }
        return tv
    }

    private fun chevron(): View {
        val tv = TextView(this)
        tv.text = "›"
        tv.setTextColor(skin.hint)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        return tv
    }

    /** থিমের কার্ড: থিমের নিজের রঙে ছোট কী-র নমুনাসহ। */
    private fun themeCard(t: KeyTheme): View {
        val selected = t.id == prefs.themeId
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        val bg = GradientDrawable()
        bg.cornerRadius = dp(14).toFloat()
        bg.setColor(t.bg)
        bg.setStroke(
            dp(if (selected) 3 else 1),
            if (selected) t.accent else Color.argb(90, 128, 128, 128)
        )
        card.background = bg

        val mini = LinearLayout(this)
        mini.orientation = LinearLayout.HORIZONTAL
        mini.gravity = Gravity.CENTER
        for (i in 0 until 3) {
            val k = View(this)
            val kb = GradientDrawable()
            kb.cornerRadius = dp(3).toFloat()
            kb.setColor(
                if (i == 2) t.accent
                else if (t.key != 0) t.key
                else Color.argb(110, 255, 255, 255)
            )
            k.background = kb
            mini.addView(
                k,
                LinearLayout.LayoutParams(dp(16), dp(10)).apply { setMargins(dp(2), 0, dp(2), 0) }
            )
        }
        card.addView(mini)

        val name = TextView(this)
        name.text = (if (selected) "✓ " else "") + t.name
        name.setTextColor(t.text)
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        name.gravity = Gravity.CENTER
        name.isSingleLine = true
        name.setPadding(dp(4), dp(6), dp(4), 0)
        card.addView(name)

        card.layoutParams = LinearLayout.LayoutParams(0, dp(64), 1f)
            .apply { setMargins(dp(4), dp(4), dp(4), dp(4)) }
        card.isClickable = true
        card.setOnClickListener {
            tick(it)
            chooseTheme(t.id)
        }
        return card
    }

    private fun fontChip(f: Fonts.FontOpt): View {
        val selected = f.id == prefs.fontId
        val tv = TextView(this)
        tv.text = f.name
        tv.typeface = Fonts.typeface(f.id)
        tv.gravity = Gravity.CENTER
        tv.isSingleLine = true
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tv.setTextColor(if (selected) Color.WHITE else skin.text)
        val bg = GradientDrawable()
        bg.cornerRadius = dp(20).toFloat()
        bg.setColor(if (selected) skin.accent else cardColor())
        tv.background = bg
        tv.layoutParams = LinearLayout.LayoutParams(0, dp(38), 1f)
            .apply { setMargins(dp(4), dp(3), dp(4), dp(3)) }
        tv.isClickable = true
        tv.setOnClickListener {
            tick(it)
            chooseFont(f.id)
        }
        return tv
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
            toolSel = -1
            fillStrip()
        })
        val micLabel = if (prefs.voiceLang == "bn-BD" || page == Page.BANGLA) "🎤ব" else "🎤"
        row.addView(fixedKey(micLabel, listening, onLong = { toggleVoiceLang() }) { toggleVoice() })
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
        var n = 0
        fun add(label: String, active: Boolean, action: () -> Unit) {
            val idx = n++
            inner.addView(fixedKey(label, active) {
                toolSel = idx
                action()
            })
        }
        add("0-9", false) { go(Page.DIGITS) }
        add("☺", false) { go(Page.EMOJI) }
        add("🖼", false) { go(Page.STICKER) }
        add("বাং", false) { go(Page.BANGLA) }
        add("📋", false) { go(Page.CLIP) }
        add("✎", false) { go(Page.EDIT) }
        add("123", false) { go(Page.NUMPAD) }
        add("∑π", false) { go(Page.SYMBOLS) }
        add("x²", script == Script.SUP) { toggleScript(Script.SUP) }
        add("x₂", script == Script.SUB) { toggleScript(Script.SUB) }
        add("◐", false) { go(Page.THEME) }
        val sv = hScroll(inner)
        if (toolSel in 0 until n) frontScroll(sv, inner.getChildAt(toolSel))
        return sv
    }

    /** [target] বাটনটা বারের একদম সামনে (বাঁয়ে) এনে দেখায়; পরের বাটনগুলো ক্রমানুসারে ওর পাশেই থাকে। */
    private fun frontScroll(sv: HorizontalScrollView, target: View) {
        sv.post { sv.scrollTo(maxOf(0, target.left - dp(4)), 0) }
    }

    private fun quickScroll(): View {
        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.HORIZONTAL
        inner.addView(fixedKey("x²", script == Script.SUP) { toggleScript(Script.SUP) })
        inner.addView(fixedKey("x₂", script == Script.SUB) { toggleScript(Script.SUB) })
        QUICK.forEach { s -> inner.addView(fixedKey(s, false, skin.text) { commitSymbol(s) }) }
        return hScroll(inner)
    }

    private fun hScroll(inner: View): HorizontalScrollView =
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
        onLong: (() -> Unit)? = null,
        onTap: () -> Unit
    ): View {
        val v = key(label, 1f, if (active) Kind.ACCENT else Kind.FLAT, 20f, if (active) Color.WHITE else textColor, onLong = onLong, onTap = onTap)
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
            val isPaste = ch == 'v'
            val h = if (isPaste) "⎘" else hints[i].toString()
            val v = key(
                t, 1f, Kind.KEY, 25f, hint = h,
                onLong = { if (isPaste) pasteClip() else typeKey(h) }
            ) { typeKey(t) }
            v.tag = ch.lowercaseChar()
            v
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
                    tick(view)
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
            tick(it)
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
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(rowH()))
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
        val sv = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(STRIP_H)
            )
            addView(tabs)
        }
        // নির্বাচিত ট্যাব বারের সামনে আসবে, বাকিগুলো ওর পরে ক্রমানুসারে
        if (selected in 0 until tabs.childCount) frontScroll(sv, tabs.getChildAt(selected))
        return sv
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
                dp(rowH() * 3)
            )
            addView(content)
        }

    private fun listKey(text: String, onTap: () -> Unit): View {
        val f = FrameLayout(this)
        f.isClickable = true
        f.background = keyBackground(if (skin.key != 0) skin.key else skin.func)
        f.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(rowH() - 8)
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
            tick(it)
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

    /** সারির উচ্চতা (dp), সেটিংয়ের শতাংশ অনুযায়ী। */
    private fun rowH(): Int = ROW_H * prefs.heightPct / 100

    /** কী চাপার ভাইব্রেশন/সাউন্ড (সেটিং অনুযায়ী)। */
    private fun tick(v: View) {
        if (prefs.haptic) v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        if (prefs.sound) {
            try {
                (getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.5f)
            } catch (e: Throwable) {
                // সাউন্ড না বাজলে সমস্যা নেই
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // ---------------------------------------------------------------- voice typing

    /** 🎤 চাপলে চালু; আবার 🎤 না চাপা পর্যন্ত (কথা থামলেও) নিজে থেকে শুনতে থাকে। */
    private fun toggleVoice() {
        if (listening) {
            stopVoice()
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
        voiceFails = 0
        listening = true
        fillStrip()
        startVoice()
    }

    /** হাতে বন্ধ করা: শেষ বলা কথাটা বসে যাবে, তারপর আর শুনবে না। */
    private fun stopVoice() {
        listening = false
        handler.removeCallbacks(voiceRestart)
        try {
            recognizer?.stopListening()
        } catch (e: Throwable) {
            // বন্ধ না হলেও সমস্যা নেই
        }
        fillStrip()
    }

    private fun scheduleVoiceRestart(delayMs: Long) {
        handler.removeCallbacks(voiceRestart)
        handler.postDelayed(voiceRestart, delayMs)
    }

    /** 🎤 চেপে ধরলে ভয়েসের ভাষা English ⇄ বাংলা। */
    private fun toggleVoiceLang() {
        prefs.voiceLang = if (prefs.voiceLang == "bn-BD") "en-US" else "bn-BD"
        toast(if (prefs.voiceLang == "bn-BD") "Voice: বাংলা" else "Voice: English")
        fillStrip()
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
                if (!listening) {
                    fillStrip()
                    return
                }
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        voiceFails = 0
                        scheduleVoiceRestart(250)
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
                    SpeechRecognizer.ERROR_CLIENT -> {
                        voiceFails++
                        if (voiceFails > 4) {
                            listening = false
                            fillStrip()
                            toast("Voice typing stopped")
                        } else {
                            scheduleVoiceRestart(600)
                        }
                    }
                    else -> {
                        listening = false
                        fillStrip()
                        toast("Voice typing error ($error)")
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    currentInputConnection?.commitText("$text ", 1)
                }
                voiceFails = 0
                if (listening) scheduleVoiceRestart(150) else fillStrip()
                updateSuggestions()
            }
        })
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (page == Page.BANGLA) "bn-BD" else prefs.voiceLang)
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        try {
            r.startListening(i)
        } catch (e: Throwable) {
            listening = false
            fillStrip()
            toast("Voice typing error")
        }
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

    /** `v` চেপে ধরলে: সবশেষ কপি করা লেখা বসে যায়। */
    private fun pasteClip() {
        val text = try {
            val clip = clipboard?.primaryClip
            if (clip != null && clip.itemCount > 0) clip.getItemAt(0).coerceToText(this)?.toString() else null
        } catch (e: Exception) {
            null
        }
        if (text.isNullOrEmpty()) toast("Clipboard is empty") else commit(text)
    }

    /** Copy: নির্বাচন/কার্সর যেখানে ছিল সেখানেই থাকে (সিস্টেমের copy অনেক জায়গায় কার্সর শুরুতে নিয়ে যায়)। */
    private fun copySelection() {
        val ic = currentInputConnection ?: return
        val sel = ic.getSelectedText(0)
        if (!sel.isNullOrEmpty()) {
            clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("text", sel))
            toast("Copied")
        } else {
            ic.performContextMenuAction(android.R.id.copy)
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
        if (swipeAlt != null && page == Page.LETTERS) return
        val next: List<String> =
            if (!learning || bangla.enabled || script != Script.NONE || page != Page.LETTERS) {
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
        if (swipeAlt != null) {
            replaceSwipe(word)
            return
        }
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
        bangla.finish(currentInputConnection)
        lastCorrection = null
        swipeAlt = null
        currentInputConnection?.commitText(s, 1)
    }

    /** সিম্বল/টেমপ্লেট বসায়; [Sym.back] থাকলে কার্সর ভেতরে সরায়। */
    private fun commitSym(sym: Sym) {
        commitSymbol(sym.insert)
        repeat(sym.back) { pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT) }
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
        if (bangla.enabled && s.length == 1 && s[0].isLetter() && script == Script.NONE) {
            bangla.typeLetter(currentInputConnection, s)
            if (shift && !caps) {
                shift = false
                render()
            }
            return
        }
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
        if (bangla.enabled) {
            commit(" ")
            return
        }
        val before = textBefore(48)
        if (expandShortcut(before)) return
        if (autoCorrect(before)) return
        learnCurrent()
        commit(" ")
        updateSuggestions()
    }

    // ---------------------------------------------------------------- shortcuts / autocorrect / swipe

    private fun shortcuts(): Map<String, String> {
        val raw = prefs.shortcutsRaw
        if (raw != shortcutRaw) {
            shortcutRaw = raw
            shortcutCache = prefs.shortcuts()
        }
        return shortcutCache
    }

    /** শর্টকাট লিখে space চাপলে পুরো লেখা বসায়। */
    private fun expandShortcut(before: String): Boolean {
        val map = shortcuts()
        if (map.isEmpty() || before.isEmpty()) return false
        var i = before.length
        while (i > 0 && !before[i - 1].isWhitespace()) i--
        val token = before.substring(i)
        if (token.isEmpty()) return false
        val expansion = map[token] ?: return false
        val ic = currentInputConnection ?: return false
        ic.beginBatchEdit()
        ic.deleteSurroundingText(token.length, 0)
        ic.commitText("$expansion ", 1)
        ic.endBatchEdit()
        lastCorrection = null
        swipeAlt = null
        updateSuggestions()
        return true
    }

    /** space চাপলে ভুল বানান (১টা অক্ষর ভুল) ঠিক করে। ব্যাকস্পেস দিলে আগের শব্দ ফিরে আসে। */
    private fun autoCorrect(before: String): Boolean {
        if (!prefs.autocorrect || !learning || script != Script.NONE || page != Page.LETTERS) return false
        val i = prefixStart(before)
        val word = before.substring(i)
        if (word.length < 3) return false
        if (i > 0 && !before[i - 1].isWhitespace()) return false
        if (word.drop(1).any { it.isUpperCase() }) return false
        val fix = store.correct(word) ?: return false
        val out = capLike(fix, word)
        val ic = currentInputConnection ?: return false
        val prev = previousWord(before.substring(0, i))
        ic.beginBatchEdit()
        ic.deleteSurroundingText(word.length, 0)
        ic.commitText("$out ", 1)
        ic.endBatchEdit()
        store.learn(out, prev)
        lastCorrection = Pair(word, out)
        swipeAlt = null
        updateSuggestions()
        return true
    }

    /** swipe-এর পথ থেকে সম্ভাব্য শব্দ (সবচেয়ে ভালোটা আগে)। */
    private fun swipeCandidates(s: String, corners: List<Char>): List<String> {
        val first = s[0]
        val last = s[s.length - 1]
        val scored = ArrayList<Pair<Int, String>>()
        for (w in store.allWords()) {
            if (w.length < 2 || w[0] != first || w[w.length - 1] != last) continue
            // পরপর একই অক্ষর (hello → helo) একটা ধরি, কারণ আঙুল একই কী-তে একবারই যায়
            val sb = StringBuilder()
            for (ch in w) if (sb.isEmpty() || sb[sb.length - 1] != ch) sb.append(ch)
            val wl = sb.toString()
            if (wl.length > s.length) continue
            var idx = 0
            for (ch in s) if (idx < wl.length && wl[idx] == ch) idx++
            if (idx < wl.length) continue
            var j = 0
            var ok = true
            for (c in corners) {
                var found = false
                while (j < wl.length) {
                    if (wl[j] == c) {
                        found = true
                        j++
                        break
                    }
                    j++
                }
                if (!found) {
                    ok = false
                    break
                }
            }
            if (!ok) continue
            val extra = s.length - wl.length
            scored.add(Pair(store.scoreOf(w) * 10 - extra * 4 + wl.length, w))
        }
        return scored.sortedByDescending { it.first }.map { it.second }.take(3)
    }

    /** আঙুল টেনে শব্দ লেখা শেষ হলে ডাকা হয়। */
    private fun onSwipeWord(keys: List<Char>, corners: List<Char>) {
        if (page != Page.LETTERS || script != Script.NONE) return
        val s = keys.joinToString("")
        if (s.length < 2) return
        val cands = swipeCandidates(s, corners)
        if (cands.isEmpty()) return
        val ic = currentInputConnection ?: return
        val up = shift || caps
        val words = cands.map { w -> if (up) w.replaceFirstChar { c -> c.uppercaseChar() } else w }
        val before = textBefore(2)
        learnCurrent()
        val sb = StringBuilder()
        if (before.isNotEmpty() && !before[before.length - 1].isWhitespace() &&
            before[before.length - 1] !in "([{\"'"
        ) sb.append(' ')
        sb.append(words[0]).append(' ')
        ic.commitText(sb.toString(), 1)
        if (learning) store.learn(words[0].lowercase())
        lastCorrection = null
        swipeAlt = words
        swipeText = words[0] + " "
        suggestions = words
        if (shift && !caps) {
            shift = false
            render()
        } else {
            fillStrip()
        }
    }

    /** swipe করা শব্দের জায়গায় অন্য বিকল্প বসায়। */
    private fun replaceSwipe(word: String) {
        val ic = currentInputConnection ?: return
        if (textBefore(swipeText.length) != swipeText) {
            swipeAlt = null
            updateSuggestions()
            return
        }
        ic.beginBatchEdit()
        ic.deleteSurroundingText(swipeText.length, 0)
        ic.commitText("$word ", 1)
        ic.endBatchEdit()
        swipeText = "$word "
        if (learning) store.learn(word.lowercase())
    }

    private fun pressEnter() {
        lastCorrection = null
        swipeAlt = null
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
        val now = SystemClock.uptimeMillis()
        if (selectMode) {
            // আসল Shift চাপার মতো: Shift ↓, তীর ↓↑, Shift ↑ — তাই মার্ক করা অংশ বাড়ে, কার্সর শুরুতে ফেরে না
            val m = KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, m))
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0, m))
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0, m))
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0))
        } else {
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0, 0))
            ic.sendKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0, 0))
        }
    }

    // ভলিউম বাটন = কার্সর সরানো (কীবোর্ড খোলা থাকলে): Vol ▲ বাঁয়ে, Vol ▼ ডানে
    private fun isVolumeKey(code: Int) =
        code == KeyEvent.KEYCODE_VOLUME_UP || code == KeyEvent.KEYCODE_VOLUME_DOWN

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isVolumeKey(keyCode) && ::prefs.isInitialized && prefs.volCursor && isInputViewShown) {
            pressKeyCode(
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) KeyEvent.KEYCODE_DPAD_LEFT
                else KeyEvent.KEYCODE_DPAD_RIGHT
            )
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (isVolumeKey(keyCode) && ::prefs.isInitialized && prefs.volCursor && isInputViewShown) return true
        return super.onKeyUp(keyCode, event)
    }

    private fun backspace() {
        val lc = lastCorrection
        if (lc != null) {
            lastCorrection = null
            val ic = currentInputConnection
            val applied = lc.second + " "
            if (ic != null && textBefore(applied.length) == applied) {
                // অটোকারেক্ট বাতিল: আসল শব্দ ফেরত আসে, আর ওটা পরিচিত শব্দ হয়ে যায়
                ic.beginBatchEdit()
                ic.deleteSurroundingText(applied.length, 0)
                ic.commitText(lc.first, 1)
                ic.endBatchEdit()
                store.learn(lc.first)
                store.learn(lc.first)
                updateSuggestions()
                return
            }
        }
        swipeAlt = null
        doBackspace()
        updateSuggestions()
    }

    private fun doBackspace() {
        if (bangla.backspace(currentInputConnection)) return
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

        /** এই সেটিংগুলো বদলালে কীবোর্ড তখনই নতুন করে সাজে (ইমোজি ইতিহাস ইত্যাদিতে নয়)। */
        val LIVE_KEYS = setOf("theme", "font", "bg_rev", "height_pct", "voice_lang")

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
