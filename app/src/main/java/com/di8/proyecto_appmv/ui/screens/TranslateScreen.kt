package com.di8.proyecto_appmv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
// Nuevas agregadas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// Agregada apenas
import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import com.di8.proyecto_appmv.data.translate.Langs
import com.di8.proyecto_appmv.data.translate.ModelState
import com.di8.proyecto_appmv.data.translate.TranslationRepository
import com.di8.proyecto_appmv.util.copyToClipboard
import com.di8.proyecto_appmv.util.shareText
import com.di8.proyecto_appmv.R

import kotlinx.coroutines.launch
// Agregada recien
import java.util.Locale


// -- Nuevo: Por defecto segun idioma de la app --
private fun defaultPairForAppLang(appLang: String): Pair<String, String> {
    val resolved = when (appLang) {
        "es","en" -> appLang
        else -> Locale.getDefault().language.take(2) // para system
    }
    // Si la app está en ES: traducimos EN -> ESen otro caso: ES -> EN
    return if (resolved == "es") "en" to "es" else "es" to "en"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(
    prefs: PreferencesRepository,
    repo: TranslationRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current      // Contexto para copiar / compartir

    // NEW: leemos el idioma elegido para la app
    val appLang by prefs.appLanguageFlow.collectAsState(initial = "system")

    // Prepara las cadenas en el scope composable
    val txtPreparing = stringResource(R.string.status_preparing)
    val txtReady = stringResource(R.string.status_ready)
    val txtDone = stringResource(R.string.status_done)
    val txtGenericErr = stringResource(R.string.error_generic)

    // -- FALTA CLAVE: respeta cambios manuales --
    var userOverrode by rememberSaveable { mutableStateOf(false) }

    // Par de idiomas (sin Key no se resetea automáticamente
    var src by rememberSaveable { mutableStateOf("en") }
    var tgt by rememberSaveable { mutableStateOf("es") }

    // Ajustar par por defecto SOLO si el usuario no lo ha cambiado
    LaunchedEffect(appLang) {
        if (!userOverrode) {
            val (s, t) = defaultPairForAppLang(appLang)
            src = s; tgt = t
        }
    }

    // Texto de entrada / salida (no se necesitan reiniciarse por idioma
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    // ESTADOS
    var isTranslating by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.translate_title)) },
                actions = {
                    // Botón SWAP (marca que el usuario tomó control)
                    IconButton(onClick = {
                        val tmp = src
                        src = tgt
                        tgt = tmp
                        if (output.isNotBlank()) {
                            input = output
                            output = ""
                        }
                        userOverrode = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = stringResource(R.string.cdesc_swap)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Copiar al portapapeles
                IconButton(
                    enabled = output.isNotBlank(),
                    onClick = { copyToClipboard(context, output) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.cdesc_copy)
                    )
                }

                // Compartir con otras apps
                IconButton(
                    enabled = output.isNotBlank(),
                    onClick = { shareText(context, output) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.cdesc_share)
                    )
                }

                // Guardar como favorito en Room
                IconButton(
                    enabled = output.isNotBlank(),
                    onClick = {
                        scope.launch {
                            repo.savePhrase(
                                src = src,
                                tgt = tgt,
                                inText = input,
                                outText = output,
                                fav = true
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.cdesc_favorite)
                    )
                }
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
                .imePadding(),      // 1) Levanta el contenido con el teclado
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila de idiomas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${Langs.displayName(src)} (${src.uppercase()})")
                Text(stringResource(R.string.arrow_right))
                Text("${Langs.displayName(tgt)} (${tgt.uppercase()})")
            }

            // Campo de entrada "elástico" y scrollable interno
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),    // 2) ocupa el espacio libre a Column
                label = { Text(stringResource(R.string.hint_input)) },
                minLines = 2,               // 3) deja crecer; evita max rígido
                maxLines = 8                // (opcional) para poner un tope amigable
            )

            // Botón TRADUCIR
            Button(
                onClick = {
                    scope.launch {
                        if (input.isBlank()) return@launch

                        isTranslating = true
                        error = null
                        status  = txtPreparing

                        when (val ms = repo.ensureModel(src, tgt)) {
                            is ModelState.Ready -> {
                                status = txtReady

                                val res = repo.translate(src, tgt, input)
                                if (res.isSuccess) {
                                    output = res.getOrThrow()
                                    status = txtDone

                                    repo.savePhrase(
                                        src = src,
                                        tgt = tgt,
                                        inText = input,
                                        outText = output,
                                        fav = false
                                    )
                                } else {
                                    error = res.exceptionOrNull()?.localizedMessage ?: txtGenericErr
                                }
                            }

                            is ModelState.Error -> {
                                error = ms.message
                                status = null
                            }
                        }

                        isTranslating = false
                    }
                },
                enabled = !isTranslating && input.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.btn_translate))
            }

            // Mensajes de estado / error
            status?.let { Text(it) }
            error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Tarjeta de resultado
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (output.isBlank())
                        stringResource(R.string.msg_translation_placeholder)
                    else output,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}