package com.di8.proyecto_appmv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource

import com.di8.proyecto_appmv.data.db.Phrase
import com.di8.proyecto_appmv.data.translate.TranslationRepository
import com.di8.proyecto_appmv.R

@Composable
fun SavedScreen(repo: TranslationRepository) {
    var tab by remember { mutableStateOf(0) }
    val history by repo.history().collectAsState(initial = emptyList())
    val favorites by repo.favorites().collectAsState(initial = emptyList())

    // IDs de strings
    val pages = listOf(R.string.history, R.string.favorites)

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            pages.forEachIndexed { i, resId ->
                Tab(selected = i == tab,
                    onClick = { tab = i },
                    text = { Text(stringResource(resId)) })
            }
        }
        val data = if (tab == 0) history else favorites
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data) { p -> PhraseItem(p) }
        }
    }
}

@Composable
private fun PhraseItem(p: Phrase) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${p.srcLang.uppercase()} -> ${p.tgtLang.uppercase()}", style = MaterialTheme.typography.labelMedium)
            Text(p.srcText, style = MaterialTheme.typography.bodyMedium)
            Divider()
            Text(p.tgtText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}