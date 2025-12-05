package com.di8.proyecto_appmv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Esquema de colores claro
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // Tambien se puede agregar mas colores para (background, surface, etc.)
)

/*
    Tema "por defecto" del proyecto (como el que Genera Android Studio).
    Aunque en este momento no lo usamos en el MainActivity por que estamos usando AppTheme,
    Pero no esta demas tenerlo.
 */

@Composable
fun Proyecto_AppMvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // <- este Typography viene  de Type.kt
        content = content
    )
}
