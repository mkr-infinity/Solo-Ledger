package com.solo.ledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.solo.ledger.ui.theme.themes.DarkCredTheme
import com.solo.ledger.ui.theme.themes.DarkMidnightTheme
import com.solo.ledger.ui.theme.themes.DarkNebulaTheme
import com.solo.ledger.ui.theme.themes.DarkObsidianTheme
import com.solo.ledger.ui.theme.themes.LightEmeraldTheme
import com.solo.ledger.ui.theme.themes.LightLavenderTheme
import com.solo.ledger.ui.theme.themes.LightOceanTheme
import com.solo.ledger.ui.theme.themes.LightRoseGoldTheme

val LocalExtendedColors = staticCompositionLocalOf { DarkCredTheme.extendedColors }
val LocalThemeDefinition = staticCompositionLocalOf<ThemeDefinition> { DarkCredTheme }

object ThemeRegistry {
    val allThemes: Map<String, ThemeDefinition> = mapOf(
        "dark_cred" to DarkCredTheme,
        "dark_midnight" to DarkMidnightTheme,
        "dark_obsidian" to DarkObsidianTheme,
        "dark_nebula" to DarkNebulaTheme,
        "light_emerald" to LightEmeraldTheme,
        "light_ocean" to LightOceanTheme,
        "light_rosegold" to LightRoseGoldTheme,
        "light_lavender" to LightLavenderTheme
    )

    fun getTheme(id: String): ThemeDefinition = allThemes[id] ?: DarkCredTheme
    fun getAllThemesList(): List<ThemeDefinition> = allThemes.values.toList()
}

@Composable
fun SoloLedgerTheme(
    themeId: String = "dark_cred",
    content: @Composable () -> Unit
) {
    val theme = ThemeRegistry.getTheme(themeId)
    CompositionLocalProvider(
        LocalExtendedColors provides theme.extendedColors,
        LocalThemeDefinition provides theme
    ) {
        MaterialTheme(
            colorScheme = theme.colorScheme,
            shapes = theme.shapes,
            content = content
        )
    }
}

val extendedColors: ExtendedColors
    @Composable get() = LocalExtendedColors.current

val themeDefinition: ThemeDefinition
    @Composable get() = LocalThemeDefinition.current
