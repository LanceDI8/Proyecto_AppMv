package com.di8.proyecto_appmv.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Evita fallos grafos anidados
import androidx.navigation.NavGraph.Companion.findStartDestination

import com.di8.proyecto_appmv.R
import com.di8.proyecto_appmv.data.prefs.PreferencesRepository
import com.di8.proyecto_appmv.data.translate.TranslationRepository

import com.di8.proyecto_appmv.ui.screens.SavedScreen
import com.di8.proyecto_appmv.ui.screens.SettingsScreen
import com.di8.proyecto_appmv.ui.screens.TranslateScreen


// Destinos de navegación con ruta y texto de la tab
sealed class Dest(val route: String, val labelRes: Int) {
    object Translate : Dest("translate", R.string.tab_translate)
    object Saved : Dest("saved", R.string.tab_saved)
    object Settings : Dest("settings", R.string.tab_settings)
}

@Composable
fun AppRoot(
    prefs: PreferencesRepository,
    repo: TranslationRepository
) {
    val navController = rememberNavController()
    val items = listOf(Dest.Translate, Dest.Saved, Dest.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute =navBackStackEntry?.destination?.route

                items.forEach { dest ->
                    val selected = currentRoute == dest.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(dest.route) {
                                    // Volver al inicio guardando estado
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        // IMPORTANTE: en Material3 el parámetro es "icon"
                        icon = {
                            when (dest) {
                                Dest.Translate -> Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = null
                                )

                                Dest.Saved -> Icon(
                                    imageVector = Icons.Filled.Bookmarks,
                                    contentDescription = null
                                )

                                Dest.Settings -> Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(id = dest.labelRes),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Translate.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.Translate.route) {
                TranslateScreen(prefs = prefs, repo = repo)
            }
            composable(Dest.Saved.route) {
                SavedScreen(repo = repo)
            }
            composable(Dest.Settings.route) {
                SettingsScreen(prefs = prefs, repo = repo)
            }
        }
    }
}