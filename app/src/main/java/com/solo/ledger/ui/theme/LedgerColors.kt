package com.solo.ledger.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Extended color roles beyond Material's ColorScheme so cards, charts, accents and
 * semantic colors stay consistent across every theme.
 */
data class LedgerColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val muted: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val onPrimary: Color,
    val outline: Color,
    val chart: List<Color>
)

object LedgerPalettes {
    // ---- Ledger Light (CONFIRMED) ----
    val LedgerLight = LedgerColors(
        primary = Color(0xFF16A34A),
        secondary = Color(0xFF4ADE80),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF8FAFC),
        card = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569),
        muted = Color(0xFF64748B),
        success = Color(0xFF16A34A),
        warning = Color(0xFFD97706),
        error = Color(0xFFDC2626),
        onPrimary = Color(0xFFFFFFFF),
        outline = Color(0xFFE2E8F0),
        chart = listOf(
            Color(0xFF16A34A), Color(0xFF4ADE80), Color(0xFF65A30D),
            Color(0xFFCA8A04), Color(0xFFEA580C), Color(0xFF0EA5A4)
        )
    )

    // ---- Ledger Dark (CONFIRMED) ----
    val LedgerDark = LedgerColors(
        primary = Color(0xFF22C55E),
        secondary = Color(0xFF4ADE80),
        background = Color(0xFF0B0F0C),
        surface = Color(0xFF121815),
        card = Color(0xFF1A211D),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFCBD5E1),
        muted = Color(0xFF94A3B8),
        success = Color(0xFF22C55E),
        warning = Color(0xFFF59E0B),
        error = Color(0xFFEF4444),
        onPrimary = Color(0xFF06210F),
        outline = Color(0xFF26312B),
        chart = listOf(
            Color(0xFF22C55E), Color(0xFF4ADE80), Color(0xFF84CC16),
            Color(0xFFF59E0B), Color(0xFFFB923C), Color(0xFF2DD4BF)
        )
    )

    // ---- Emerald Light (CONFIRMED, no-blue jewel-emerald + warm stone neutrals) ----
    val EmeraldLight = LedgerColors(
        primary = Color(0xFF047857),
        secondary = Color(0xFF34D399),
        background = Color(0xFFFCFCFB),
        surface = Color(0xFFF5F5F4),
        card = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF1C1917),
        textSecondary = Color(0xFF44403C),
        muted = Color(0xFF78716C),
        success = Color(0xFF059669),
        warning = Color(0xFFD97706),
        error = Color(0xFFDC2626),
        onPrimary = Color(0xFFFFFFFF),
        outline = Color(0xFFE7E5E4),
        chart = listOf(
            Color(0xFF047857), Color(0xFF34D399), Color(0xFF65A30D),
            Color(0xFFCA8A04), Color(0xFFEA580C), Color(0xFF0D9488)
        )
    )

    // ---- Emerald Dark (CONFIRMED) ----
    val EmeraldDark = LedgerColors(
        primary = Color(0xFF34D399),
        secondary = Color(0xFF6EE7B7),
        background = Color(0xFF0A0F0D),
        surface = Color(0xFF141A17),
        card = Color(0xFF1C2420),
        textPrimary = Color(0xFFF5F5F4),
        textSecondary = Color(0xFFD6D3D1),
        muted = Color(0xFFA8A29E),
        success = Color(0xFF34D399),
        warning = Color(0xFFFBBF24),
        error = Color(0xFFF87171),
        onPrimary = Color(0xFF052E1B),
        outline = Color(0xFF2A302C),
        chart = listOf(
            Color(0xFF34D399), Color(0xFF6EE7B7), Color(0xFF84CC16),
            Color(0xFFFBBF24), Color(0xFFFB923C), Color(0xFF2DD4BF)
        )
    )

    // ---- Anime Light (violet + sakura pink, no blue) ----
    val AnimeLight = LedgerColors(
        primary = Color(0xFF7C3AED), secondary = Color(0xFFF472B6),
        background = Color(0xFFFFFFFF), surface = Color(0xFFFAF5FF), card = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF1E1B2E), textSecondary = Color(0xFF5B5470), muted = Color(0xFF8B82A6),
        success = Color(0xFF16A34A), warning = Color(0xFFD97706), error = Color(0xFFE11D48),
        onPrimary = Color(0xFFFFFFFF), outline = Color(0xFFEDE4FB),
        chart = listOf(Color(0xFF7C3AED), Color(0xFFF472B6), Color(0xFFA78BFA), Color(0xFFFB7185), Color(0xFFFBBF24), Color(0xFF34D399))
    )
    val AnimeDark = LedgerColors(
        primary = Color(0xFFA78BFA), secondary = Color(0xFFF0ABFC),
        background = Color(0xFF0F0A1A), surface = Color(0xFF18122B), card = Color(0xFF1F1736),
        textPrimary = Color(0xFFF5F3FF), textSecondary = Color(0xFFCFC6E8), muted = Color(0xFF9A8FC0),
        success = Color(0xFF34D399), warning = Color(0xFFFBBF24), error = Color(0xFFFB7185),
        onPrimary = Color(0xFF1A1033), outline = Color(0xFF2C2348),
        chart = listOf(Color(0xFFA78BFA), Color(0xFFF0ABFC), Color(0xFFC4B5FD), Color(0xFFFB7185), Color(0xFFFBBF24), Color(0xFF6EE7B7))
    )
    // ---- Spider Light (crimson + graphite, no blue) ----
    val SpiderLight = LedgerColors(
        primary = Color(0xFFE11D48), secondary = Color(0xFF111827),
        background = Color(0xFFFFFFFF), surface = Color(0xFFFEF2F2), card = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF0A0A0A), textSecondary = Color(0xFF44403C), muted = Color(0xFF78716C),
        success = Color(0xFF16A34A), warning = Color(0xFFD97706), error = Color(0xFFB91C1C),
        onPrimary = Color(0xFFFFFFFF), outline = Color(0xFFFADCDC),
        chart = listOf(Color(0xFFE11D48), Color(0xFF111827), Color(0xFFF43F5E), Color(0xFF78716C), Color(0xFFEA580C), Color(0xFFCA8A04))
    )
    val SpiderDark = LedgerColors(
        primary = Color(0xFFF43F5E), secondary = Color(0xFFE5E7EB),
        background = Color(0xFF0A0A0B), surface = Color(0xFF141416), card = Color(0xFF1C1C1F),
        textPrimary = Color(0xFFF8FAFC), textSecondary = Color(0xFFCBD5C5), muted = Color(0xFF9CA3AF),
        success = Color(0xFF34D399), warning = Color(0xFFFBBF24), error = Color(0xFFFB7185),
        onPrimary = Color(0xFF2A0A12), outline = Color(0xFF2A2A2E),
        chart = listOf(Color(0xFFF43F5E), Color(0xFFE5E7EB), Color(0xFFFB7185), Color(0xFF9CA3AF), Color(0xFFFB923C), Color(0xFFFBBF24))
    )

    fun forId(themeId: String, dark: Boolean): LedgerColors = when (themeId) {
        "anime" -> if (dark) AnimeDark else AnimeLight
        "spider" -> if (dark) SpiderDark else SpiderLight
        "emerald" -> if (dark) EmeraldDark else EmeraldLight
        // Other theme ids (emerald/anime/spider) fall back to Ledger until confirmed by the user.
        else -> if (dark) LedgerDark else LedgerLight
    }
}
