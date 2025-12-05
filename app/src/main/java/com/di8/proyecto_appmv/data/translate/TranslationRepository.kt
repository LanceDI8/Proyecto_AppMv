package com.di8.proyecto_appmv.data.translate

import com.di8.proyecto_appmv.data.db.Phrase
import com.di8.proyecto_appmv.data.db.PhraseDao
import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await


// Estado del modelo ML kit
sealed class ModelState {
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}


/*
    Orquesta traducciones + Room + preferencias.
    Notas:
        - Respetamos el flag "solo Wi-fi desde PreferencesRepository.
        - Cerramos SIEMPRE el Translator en finally {}  para liberar recursos de ML Kit.
        - Descarga/eliminación de modelos se hacen vía RemoteModelManager
 */
class TranslationRepository(
    private val dao: PhraseDao,
    private val provider: TranslatorProvider,
        private val context: android.content.Context,               // No se usa aquí directamente; pero si se necesita en otros métodos
    private val prefs: PreferencesRepository
) {
    private val manager = RemoteModelManager.getInstance()

    // ------ Gestón de modelos (para Settings) -------

    /** Devuelve el conjunto de códigos (BCP-47) ya instalados, por ejemplo ["es", "en"] */
    suspend fun installedModels(): Set<String> {
        val downloaded = manager.getDownloadedModels(TranslateRemoteModel::class.java).await()
        return downloaded.mapNotNull { it.language }.toSet()
    }

    /** Descarga el modelo de un idioma (respeta el flag "solo Wi-Fi") */
    suspend fun downloadModel(langCode: String): Result<Unit> = runCatching {
        val tag = TranslateLanguage.fromLanguageTag(langCode)
            ?: error("Idioma no soportado: $langCode")
        val model = TranslateRemoteModel.Builder(tag).build()

        val wifiOnly = prefs.wifiOnlyFlow.first()
        val conditions = DownloadConditions.Builder().apply { if (wifiOnly) requireWifi() }.build()

        manager.download(model, conditions).await()
    }

    /*
        Elimina el modelo descargado de un idioma.
        (Devuelve Unit y lanza excepción si falla).
     */
    suspend fun deleteModel(langCode: String) {
        val tag = Langs.toMlKit(langCode)       // aseguramos tag válido
        val model = TranslateRemoteModel.Builder(tag).build()
        manager.deleteDownloadedModel(model).await()
    }

    // ------------ Flujo de traducción -------------

    /*
        Asegura que el par (src, tgt) tenga los modelos descargados/listos.
        Se puede usar antes de traducir si no se esta seguro del estado local.
    */
    suspend fun ensureModel(src: String, tgt: String): ModelState {
        val translator = provider.get(src, tgt)
        val wifiOnly = prefs.wifiOnlyFlow.first()
        val conditions = DownloadConditions.Builder().apply { if (wifiOnly) requireWifi() }.build()

        return try {
            translator.downloadModelIfNeeded(conditions).await()
            ModelState.Ready
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ModelState.Error(e.localizedMessage ?: "Error al preparar/descargar modelo")
        } finally {
            translator.close()
        }
    }

    /*
        * Traduce un texto. Asume `ensureModel` ya se realizó si hacía falta descargar.
        * Devuelve Result<String> para manejar éxito/fracaso en la UI
     */
    suspend fun translate(src: String, tgt: String, text: String): Result<String> {
        val translator = provider.get(src, tgt)
        return try {
            val translated = translator.translate(text).await()
            Result.success(translated)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            translator.close()
        }
    }

    // ---------- Persistencia (Room) -----------
    suspend fun savePhrase(
        src: String,
        tgt: String,
        inText: String,
        outText: String,
        fav: Boolean
    ) {
        dao.insert(
            Phrase(
                srcLang = src,
                tgtLang = tgt,
                srcText = inText,
                tgtText = outText,
                isFavorite = fav
            )
        )
    }

    fun history() = dao.observeHistory()
    fun favorites() = dao.observeFavorites()



}