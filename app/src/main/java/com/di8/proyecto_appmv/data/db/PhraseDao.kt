package com.di8.proyecto_appmv.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Insert suspend fun insert(p: Phrase): Long
    @Update suspend fun update(p: Phrase)
    @Delete suspend fun delete(p: Phrase)

    @Query("SELECT * FROM phrases ORDER BY createdAt DESC")
    fun observeHistory(): Flow<List<Phrase>>

    @Query("SELECT * FROM phrases WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<Phrase>>

    @Query("""
        SELECT * FROM phrases
        WHERE srcText LIKE '%' || :q || '%' OR tgtText LIKE '%' || :q || '%'
        ORDER BY createdAt DESC
    """)
    fun search(q: String): Flow<List<Phrase>>
}