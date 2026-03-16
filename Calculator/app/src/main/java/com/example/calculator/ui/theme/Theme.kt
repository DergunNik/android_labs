package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,

    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,

    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,

    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground
)

private val DarkColorScheme = darkColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,

    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,

    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,

    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}