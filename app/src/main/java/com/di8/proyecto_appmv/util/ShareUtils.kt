package com.di8.proyecto_appmv.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

import androidx.core.content.ContextCompat

/*
    Copia un texto al portapapeles del sistema.

    @param context  lo usamos para acceder al Clipboard y para el Toast
    @param text  el texto que queremos copiar
 */

fun copyToClipboard(context: Context, text: String) {
    // Si el texto viene vacío, no se hace nada
    if(text.isBlank()) return

    // Obtenemos el servicio de portapapeles de Android
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)


    // Creamos un "Clip" (como un bloque de texto) como una etiqueta y el contenido
    val clip = ClipData.newPlainText("translation", text)

    // Lo ponemos como clip principal (lo que el usuario puede pegar)
    clipboard?.setPrimaryClip(clip)

    // Mensajito corto para avisar al usuario
    Toast.makeText(context, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
}

/*
    Lanza el menú de compartir de Android con un texto.

    @param context  se usa para lanzar el Intent
    @param text  el texto que queremos compartir
 */

fun  shareText(context: Context, text: String) {
    // Igual, si no hay texto, salimos
    if (text.isBlank()) return

    // Creamos un Intent de "enviar" texto plano
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"                                 // tipo de contenido
        putExtra(Intent.EXTRA_TEXT, text)    // el texto que se comparte
    }

    // Envuelve el intent en un "chooser" para que el usuario elija la app
    val chooser = Intent.createChooser(sendIntent, "Compartir traducción")

    // Lanzamos la actividad del chooser
    context.startActivity(chooser)
}