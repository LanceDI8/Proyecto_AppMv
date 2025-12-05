package com.di8.proyecto_appmv.data.translate

import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

/*
    * Crea un Translator para (src, tgt),
    * OJO: El repositorio es quien lo cierra en finally {}
 */

class TranslatorProvider {
    fun get(src: String, tgt: String): Translator {
        val opts = TranslatorOptions.Builder()
            .setSourceLanguage(Langs.toMlKit(src))
            .setTargetLanguage(Langs.toMlKit(tgt))
            .build()
        return Translation.getClient(opts)
    }
}