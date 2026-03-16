package com.example.calculator.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF292929)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_secondary = Color(0xFFF18800)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_tertiary = Color(0xFF555555)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_background = Color(0xFF000000)
val md_theme_light_onBackground = Color(0xFFFFFFFF)
val md_theme_light_onBackgroundLite = Color(0xFF8B8B8D)
val md_theme_light_additional = Color(0xFF191919)

data class ExtendedColors(
    val onBackgroundLite: Color,
    val additional: Color
)

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColors> {
    error("No ExtendedColors provided")
}