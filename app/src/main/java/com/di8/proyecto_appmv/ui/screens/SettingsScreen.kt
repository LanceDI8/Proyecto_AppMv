package com.di8.proyecto_appmv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

import com.di8.proyecto_appmv.R
import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import com.di8.proyecto_appmv.data.translate.Langs
import com.di8.proyecto_appmv.data.translate.TranslationRepository
import kotlinx.coroutines.launch

// Importación helper
import com.di8.proyecto_appmv.util.applyAppLanguageNow
import com.di8.proyecto_appmv.util.findActivity

@Composable
fun SettingsScreen(
    prefs: PreferencesRepository,
    repo: TranslationRepository
) {
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }     // 1) estado del snackbar

    val ctx = LocalContext.current

    // Flows de preferencias
    val theme by prefs.themeFlow.collectAsState(initial = "light")
    val appLang by prefs.appLanguageFlow.collectAsState(initial = "system")
    val wifiOnly by prefs.wifiOnlyFlow.collectAsState(initial = true)

    // Estado local
    var installed by remember { mutableStateOf<Set<String>>(emptySet()) }
    var busyLang by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching { repo.installedModels() }
            .onSuccess { models -> installed = models }     // ASIGNA
            .onFailure { e ->
                snackbar.showSnackbar(
                    ctx.getString(R.string.err_list_models, e.localizedMessage ?: "")
                )
            }
    }

    val scroll = rememberScrollState()

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbar) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall
            )

            // Tema
            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.titleMedium
            )
            val themeOptions = listOf(
                "light" to stringResource(R.string.settings_theme_light),
                "dark"  to stringResource(R.string.settings_theme_dark),
                "system" to stringResource(R.string.settings_theme_system) // <- ESTE ID
            )
            themeOptions.forEach { (value, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = theme == value,
                        onClick = { scope.launch { prefs.setTheme(value) } }
                    )
                    Text(label, modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Idioma de la app
            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleMedium
            )
            val langOptions = listOf(
                "system" to stringResource(R.string.settings_lang_system),
                "es" to stringResource(R.string.settings_lang_es),
                "en" to stringResource(R.string.settings_lang_en)
            )
            langOptions.forEach { (value, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = appLang == value,
                        onClick = {
                            if (appLang != value) {         // <- evita doble recreación/bounce
                                scope.launch { prefs.setAppLanguage(value) }
                                applyAppLanguageNow(value)
                            }
                        }
                    )
                    Text(label, modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Solo Wi-Fi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_wifi_only),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = wifiOnly,
                    onCheckedChange = { on -> scope.launch { prefs.setWifiOnly(on) } }
                )
            }

            Divider()

            // Idiomas offline
            Text(
                text = stringResource(R.string.settings_offline_langs),
                style = MaterialTheme.typography.titleMedium
            )

            Langs.supported.forEach { code ->
                val isInstalled = code in installed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${Langs.displayName(code)} ($code)")

                    if (isInstalled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.settings_installed),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (busyLang == code) {
                                Spacer(Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(
                                enabled = busyLang != code,
                                onClick = {
                                    scope.launch {
                                        busyLang = code
                                        runCatching { repo.deleteModel(code) }
                                            .onSuccess {
                                                installed = installed - code
                                                snackbar.showSnackbar(
                                                    ctx.getString(R.string.msg_delete_ok)
                                                )
                                            }
                                            .onFailure { e ->
                                                snackbar.showSnackbar(
                                                    ctx.getString(R.string.err_delete, e.localizedMessage ?: "")
                                                    )
                                            }
                                        busyLang = null
                                    }
                                }
                            ) { Text(stringResource(R.string.settings_delete)) }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (busyLang == code) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            TextButton(
                                enabled = busyLang != code,
                                onClick = {
                                    scope.launch {
                                        busyLang = code
                                        repo.downloadModel(code)
                                            .onSuccess {
                                                installed = installed + code
                                                snackbar.showSnackbar(
                                                    ctx.getString(R.string.msg_download_ok)
                                                )
                                            }
                                            .onFailure { e ->
                                                snackbar.showSnackbar(
                                                    ctx.getString(R.string.err_download, e.localizedMessage ?: "")
                                                    )
                                            }
                                        busyLang = null
                                    }
                                }
                            ) { Text(stringResource(R.string.settings_download)) }
                        }
                    }
                }
            }
        }
    }
}