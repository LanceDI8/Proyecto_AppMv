package com.di8.proyecto_appmv.data.prefs

import android.content.Context

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// extensión de Context para DataStore
private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")           // "light" / "dark" / "system"
        val APP_LANG = stringPreferencesKey("app_lang")     // "system" / "es" / "en"
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME] ?: "light"
    }

    val appLanguageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        when (val v = prefs[Keys.APP_LANG] ?: "system") {
            "system","es","en" -> v
            else -> "system"
        }
    }

    val wifiOnlyFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.WIFI_ONLY] ?: true
    }

    // setters
    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[Keys.THEME] = value }
    }

    suspend fun setAppLanguage(value: String) {
        context.dataStore.edit { it[Keys.APP_LANG] = value }
    }

    suspend fun setWifiOnly(value: Boolean) {
        context.dataStore.edit { it[Keys.WIFI_ONLY] = value }
    }
}