package com.extempo.typescan.model.database

import androidx.room.*
import com.extempo.typescan.model.DocumentItem

@Dao
interface DocumentItemDao {
    @Query("SELECT * FROM documents")
    fun getAllDocumentItems(): List<DocumentItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(documentItem: DocumentItem)

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getDocumentItemById(id: String)

    @Query("DELETE FROM documents WHERE id = :id")
    fun deleteDocumentItemById(id: String)
}