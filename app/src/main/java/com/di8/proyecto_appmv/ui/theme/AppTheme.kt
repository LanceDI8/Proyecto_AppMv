package com.di8.proyecto_appmv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*

import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import kotlinx.coroutines.flow.collectLatest

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun AppTheme(
    prefs: PreferencesRepository,
    content: @Composable () -> Unit
) {
    val theme by prefs.themeFlow.collectAsState(initial = "light") // "light" default
    val dark = when (theme) {
        "light" -> false
        "dark" -> true
        else ->  isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}