package com.example.scikeyboard

/** একটি চিহ্ন: [label] বাটনে দেখায়, [insert] টেক্সট ফিল্ডে বসে। */
data class Sym(val label: String, val insert: String = label)

data class Category(val name: String, val items: List<Sym>)

object SymbolData {

    private fun syms(spaceSeparated: String): List<Sym> =
        spaceSeparated.trim().split(Regex("\\s+")).map { Sym(it) }

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
        )
    )
}
