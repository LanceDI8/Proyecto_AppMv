package com.di8.proyecto_appmv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.di8.proyecto_appmv.data.db.AppDatabase
import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import com.di8.proyecto_appmv.data.translate.TranslationRepository
import com.di8.proyecto_appmv.data.translate.TranslatorProvider
import com.di8.proyecto_appmv.ui.AppRoot
import com.di8.proyecto_appmv.ui.theme.AppTheme

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Importación helper
import com.di8.proyecto_appmv.util.applyAppLanguageNow

class MainActivity : ComponentActivity() {

    private val prefs by lazy { PreferencesRepository(this) }
    private val db by lazy { AppDatabase.get(this) }
    private val translatorProvider by lazy { TranslatorProvider() }
    private val translationRepo by lazy {
        TranslationRepository(
            dao = db.phraseDao(),
            provider = translatorProvider,
            context = this,
            prefs = prefs
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplica el idioma guardado ANTES de componer (pero sin crashear si algo sale mal)
        val lang = runBlocking { prefs.appLanguageFlow.first() }
        try { applyAppLanguageNow(lang) } catch (_: Throwable) { /* continúa sin caerse */ }

        setContent {
            AppTheme(prefs = prefs) {
                AppRoot(prefs = prefs, repo = translationRepo)
            }
        }
    }
}