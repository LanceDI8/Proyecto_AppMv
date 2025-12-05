package com.di8.proyecto_appmv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Phrase::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun phraseDao(): PhraseDao

    companion object {
        @Volatile private var I: AppDatabase? = null

        fun get(ctx: Context): AppDatabase =
            I ?: synchronized(this) {
                I ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )
                // Borra y recrea si cambia la versión sin migraciones
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { I = it }
            }
    }
}
