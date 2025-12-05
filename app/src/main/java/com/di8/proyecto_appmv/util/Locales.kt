package com.di8.proyecto_appmv.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun applyAppLanguageNow(code: String) {
    val locales = when (code) {
        "es" -> LocaleListCompat.forLanguageTags("es")
        "en" -> LocaleListCompat.forLanguageTags("en")
        else -> LocaleListCompat.getEmptyLocaleList()   // seguir sistema
    }
    AppCompatDelegate.setApplicationLocales(locales)
}