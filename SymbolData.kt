package com.example.scikeyboard

/**
 * একটি চিহ্ন: [label] বাটনে দেখায়, [insert] টেক্সট ফিল্ডে বসে।
 * [back] > 0 হলে বসানোর পর কার্সর এত ঘর বাঁয়ে সরে (যেমন √() এর ভেতরে)।
 */
data class Sym(val label: String, val insert: String = label, val back: Int = 0)

data class Category(val name: String, val items: List<Sym>)

object SymbolData {

    private fun syms(spaceSeparated: String): List<Sym> =
        spaceSeparated.trim().split(Regex("\\s+")).map { Sym(it) }

    /** "লেবেল¦বসবে¦পেছনে" তিন অংশের তালিকা ( ¦ দিয়ে ভাগ ); পেছনে না দিলে 0। */
    private fun tmpl(vararg items: String): List<Sym> =
        items.map {
            val p = it.split("¦")
            Sym(p[0], p.getOrElse(1) { p[0] }, p.getOrElse(2) { "0" }.toInt())
        }

    /** কম্বাইনিং অ্যাকসেন্ট: আগে টাইপ করা অক্ষরের ওপর/নিচে বসে (যেমন x̄, x⃗)। */
    private fun accents(vararg codes: Int): List<Sym> =
        codes.map { Sym("◌" + String(Character.toChars(it)), String(Character.toChars(it))) }

    val categories: List<Category> = listOf(
        Category(
            "Greek",
            syms(
                "α β γ δ ε ζ η θ ι κ λ μ ν ξ ο π ρ σ τ υ φ χ ψ ω " +
                    "ϵ ϑ ϰ ϖ ϱ ς ϕ " +
                    "Γ Δ Θ Λ Ξ Π Σ Υ Φ Ψ Ω Α Β Ε Ζ Η Ι Κ Μ Ν Ο Ρ Τ Χ"
            )
        ),
        Category(
            "Basic",
            syms(
                "± ∓ × ÷ ⋅ ∗ ∘ ∙ ⊕ ⊖ ⊗ ⊘ ⊙ − ⁄ √ ∛ ∜ ∑ ∏ ∐ ∞ ∣ ∤ ∥ ∦ ¬ ∧ ∨ ∩ ∪ ∖ ∆"
            )
        ),
        Category(
            "Relations",
            syms(
                "= ≠ ≈ ≉ ≡ ≢ ≅ ≃ ≄ ∼ ≁ ≪ ≫ ≤ ≥ ≦ ≧ ≮ ≯ ≰ ≱ ∝ ≺ ≻ ≼ ≽ " +
                    "⊂ ⊃ ⊆ ⊇ ⊄ ⊅ ∈ ∉ ∋ ∌ ⊥ ∠ ≜ ≝ ≔ ≕ ≐ ≍ ≎ ≏ ⊢ ⊣ ⊨"
            )
        ),
        Category(
            "Calculus",
            syms(
                "∫ ∬ ∭ ∮ ∯ ∰ ∑ ∏ ∐ ∂ ∇ ∆ √ ∛ ∜ ′ ″ ‴ ∞ ℓ ℏ ħ ⨌ ⨍ ∝ ≈ ⟨ ⟩ ‖ ∣"
            )
        ),
        Category(
            "Functions",
            syms(
                "sin cos tan cot sec csc arcsin arccos arctan sinh cosh tanh " +
                    "log ln exp lim max min sup inf det dim gcd lcm mod deg arg ker Pr"
            )
        ),
        Category(
            "Arrows",
            syms(
                "← ↑ → ↓ ↔ ↕ ↖ ↗ ↘ ↙ ⇐ ⇑ ⇒ ⇓ ⇔ ⇕ ↦ ↤ ↪ ↩ ⟵ ⟶ ⟷ ⟸ ⟹ ⟺ " +
                    "↼ ⇀ ⇌ ⇋ ↝ ↜ ⇝ ⟼ ↻ ↺ ↠ ↢ ↣ ↞ ⇄ ⇆ ⇉ ⇇"
            )
        ),
        Category(
            "Sets & Logic",
            syms(
                "∅ ∈ ∉ ∋ ∌ ⊂ ⊃ ⊆ ⊇ ⊄ ⊅ ⊊ ⊋ ∪ ∩ ∖ ∆ ∀ ∃ ∄ ¬ ∧ ∨ ⊻ ⊼ ⊽ ⊤ ⊥ ⊢ ⊨ ∴ ∵ ∎ ∣ " +
                    "ℕ ℤ ℚ ℝ ℂ ℙ ℍ ℵ ℶ ℷ ℸ"
            )
        ),
        Category(
            "Letters",
            syms(
                "ℂ ℍ ℕ ℙ ℚ ℝ ℤ ℊ ℋ ℌ ℎ ℏ ℐ ℑ ℒ ℓ ℘ ℛ ℜ ℨ ℬ ℭ ℯ ℰ ℱ ℳ ℴ ℵ ℶ ℷ ℸ"
            )
        ),
        Category(
            "Scripts",
            syms(
                "⁰ ¹ ² ³ ⁴ ⁵ ⁶ ⁷ ⁸ ⁹ ⁺ ⁻ ⁼ ⁽ ⁾ ⁿ ⁱ " +
                    "₀ ₁ ₂ ₃ ₄ ₅ ₆ ₇ ₈ ₉ ₊ ₋ ₌ ₍ ₎ ₐ ₑ ₒ ₓ ₕ ₖ ₗ ₘ ₙ ₚ ₛ ₜ " +
                    "ᵃ ᵇ ᶜ ᵈ ᵉ ᶠ ᵍ ʰ ʲ ᵏ ˡ ᵐ ᵒ ᵖ ʳ ˢ ᵗ ᵘ ᵛ ʷ ˣ ʸ ᶻ " +
                    "½ ⅓ ⅔ ¼ ¾ ⅕ ⅖ ⅗ ⅘ ⅙ ⅚ ⅛ ⅜ ⅝ ⅞"
            )
        ),
        Category(
            "Brackets",
            syms(
                "( ) [ ] { } ⟨ ⟩ ⌈ ⌉ ⌊ ⌋ ‖ | ⟦ ⟧ « » ⌜ ⌝ ⌞ ⌟ … ⋯ ⋮ ⋱ ⋰"
            )
        ),
        Category(
            "Accents",
            accents(
                0x0307, 0x0308, 0x20DB, 0x0302, 0x030C, 0x0301, 0x0300, 0x0306,
                0x0303, 0x0305, 0x0304, 0x20D7, 0x20D6, 0x20E1, 0x20D0, 0x20D1, 0x0332
            )
        ),
        Category(
            "Geometry",
            syms(
                "∠ ∡ ∢ ⊾ ⊿ △ ▽ □ ◇ ○ ∥ ⊥ ≅ ∼ ° ′ ″ ∟ ▲ ▼ ◊ ⌒"
            )
        ),
        Category(
            "Units",
            syms(
                "° µ Å Ω ‰ ‱ ℃ ℉ ℏ ℓ № ∞ ± × ÷ ² ³ ½ ¼ ¾"
            )
        ),
        Category(
            "Templates",
            tmpl(
                "a/b¦()/()¦4", "√( )¦√()¦1", "∛( )¦∛()¦1", "∜( )¦∜()¦1",
                "x^n¦^()¦1", "xₙ¦_()¦1", "|x|¦||¦1", "‖x‖¦‖‖¦1",
                "∑ᵢ¦∑_(i=1)^(n) ¦0", "∏ᵢ¦∏_(i=1)^(n) ¦0",
                "∫ dx¦∫ f(x) dx¦3", "∫ₐᵇ¦∫_(a)^(b) f(x) dx¦0",
                "∬¦∬ f(x,y) dx dy¦0", "∮¦∮ F·dr¦0",
                "lim¦lim_(x→0) ¦0", "lim ∞¦lim_(x→∞) ¦0",
                "d/dx¦d/dx ¦0", "∂/∂x¦∂/∂x ¦0", "dy/dx¦dy/dx¦0", "∇·F¦∇·F¦0",
                "log₂¦log_(2)()¦1", "logₙ¦log_()()¦3", "eˣ¦e^()¦1", "ln( )¦ln()¦1",
                "sin( )¦sin()¦1", "cos( )¦cos()¦1", "tan( )¦tan()¦1",
                "[ ]₂ₓ₂¦[ a b ; c d ]¦0", "[ ]₃ₓ₃¦[ a b c ; d e f ; g h i ]¦0",
                "( )ⁿᵣ¦C(n,r)¦0", "n!¦n!¦0", "P(A|B)¦P(A|B)¦0",
                "E[X]¦E[]¦1", "Var(X)¦Var()¦1", "x̄¦x̄¦0", "σ²¦σ²¦0"
            )
        ),
        Category(
            "Formulas",
            tmpl(
                "Quadratic¦x = (−b ± √(b² − 4ac)) / (2a)¦0",
                "Pythagoras¦a² + b² = c²¦0",
                "Euler¦e^(iπ) + 1 = 0¦0",
                "E = mc²¦E = mc²¦0",
                "F = ma¦F = ma¦0",
                "V = IR¦V = IR¦0",
                "sin²+cos²¦sin²θ + cos²θ = 1¦0",
                "(a+b)²¦(a + b)² = a² + 2ab + b²¦0",
                "(a−b)²¦(a − b)² = a² − 2ab + b²¦0",
                "a²−b²¦a² − b² = (a + b)(a − b)¦0",
                "Area ○¦A = πr²¦0", "Circum.¦C = 2πr¦0", "Vol. ◯¦V = (4/3)πr³¦0",
                "s = ut+½at²¦s = ut + ½at²¦0", "v = u + at¦v = u + at¦0",
                "v² = u²+2as¦v² = u² + 2as¦0", "KE¦KE = ½mv²¦0", "PE¦PE = mgh¦0",
                "P = F/A¦P = F/A¦0", "PV = nRT¦PV = nRT¦0",
                "Mean¦x̄ = (∑xᵢ) / n¦0", "Std dev¦σ = √(∑(xᵢ − x̄)² / n)¦0",
                "∫xⁿ dx¦∫ xⁿ dx = x^(n+1)/(n+1) + C¦0",
                "d(xⁿ)¦d/dx (xⁿ) = n·x^(n−1)¦0",
                "Bayes¦P(A|B) = P(B|A)·P(A) / P(B)¦0",
                "Sum 1..n¦1 + 2 + ⋯ + n = n(n+1)/2¦0"
            )
        ),
        Category(
            "Physics",
            syms(
                "ℏ ħ μ₀ ε₀ ℓ Å Ω Ω µ ° ℃ ℉ K ∝ ∇ ∇· ∇× ∂ Δ δ λ ν ω Φ Ψ ψ φ " +
                    "→ ⃗ ⟂ ∥ ⇌ ≈ ≪ ≫ ∞ ± ⊥ ⊙ ⊗ ℜ ℑ ℵ ½ ⁻¹ ⁻² ⁻³ " +
                    "m/s m/s² N·m J/s kg·m/s W/m² mol/L g/cm³ kHz MHz GHz"
            )
        ),
        Category(
            "Chemistry",
            syms(
                "H₂O CO₂ O₂ H₂ N₂ O₃ NaCl HCl NaOH KOH H₂SO₄ HNO₃ H₃PO₄ CH₄ C₂H₆ C₂H₅OH " +
                    "NH₃ NH₄⁺ CaCO₃ CaO Ca(OH)₂ Fe₂O₃ Al₂O₃ MgO SO₂ SO₃ NO₂ CO C₆H₁₂O₆ C₆H₆ " +
                    "⇌ → ← ↑ ↓ Δ ⁺ ⁻ ²⁺ ³⁺ ²⁻ ³⁻ ⁿ ° ⇄ ⟶ ⟵ ⚗ ☢ ☣ " +
                    "₀ ₁ ₂ ₃ ₄ ₅ ₆ ₇ ₈ ₉"
            )
        ),
        Category(
            "Stats & Misc",
            syms(
                "μ σ ρ χ² Σ x̄ ŷ β̂ θ̂ ∑ ∏ ≈ ∼ ≅ ≡ ∝ ‰ % ∞ ∅ ℕ ℤ ℚ ℝ ℂ " +
                    "৳ $ € £ ¥ ₹ ¢ © ® ™ § ¶ † ‡ • ‣ ⁂ № ✓ ✗ ✔ ✘ ★ ☆ ♠ ♣ ♥ ♦ ♪ ♫ ☀ ☁ ☂ ☃"
            )
        )
    )
}
