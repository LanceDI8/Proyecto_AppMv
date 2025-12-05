package com.di8.proyecto_appmv.data.translate

import com.google.mlkit.nl.translate.TranslateLanguage

/*
    * Idiomas soportados + utilidades:
    * - displayName(): nombre para la UI
    * - toMlKit(): convierte "es"/"en"/... al código que entiende ML Kit
 */
object Langs {

    // idiomas que usará la app
    val supported = listOf("es", "en", "fr", "de", "it", "pt")

    // Nombres para mostrar en la interfaz
    fun displayName(code: String) = when (code) {
        "es" -> "Español"
        "en" -> "Inglés"
        "fr" -> "Francés"
        "de" -> "Alemán"
        "it" -> "Italiano"
        "pt" -> "Portugués"
        else -> code.uppercase()
    }

    /** Devuelve el tag que extiende ML Kit (por ejemplo: "es", "en",...) o Lanza error si no soporta */
    fun toMlKit(code: String): String =
        TranslateLanguage.fromLanguageTag(code)
            ?: error("Idioma no soportado: $code")
}