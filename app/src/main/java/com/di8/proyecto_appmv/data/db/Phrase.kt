package com.di8.proyecto_appmv.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class Phrase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val srcLang: String,
    val tgtLang: String,
    val srcText: String,
    val tgtText: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)