package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CleanColorScheme = lightColorScheme(
    primary = SageGreen,
    secondary = SageLight,
    tertiary = CharcoalDark,
    background = MilkWhite,
    surface = SoftGrey,
    onPrimary = MilkWhite,
    onSecondary = CharcoalDark,
    onBackground = CharcoalDark,
    onSurface = CharcoalDark
)

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CleanColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}