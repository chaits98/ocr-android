package com.extempo.typescan.model.database

import androidx.room.*
import com.extempo.typescan.model.DocumentItem

@Dao
interface DocumentItemDao {
    @Query("SELECT * FROM documents")
    fun getAllDocumentItems(): List<DocumentItem>

    @Query("SELECT * FROM documents WHERE id == :id")
    fun getDocumentItemById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(documentItem: DocumentItem)

    @Delete
    fun deleteDocumentItem(documentItem: DocumentItem)

    @Update
    fun updateDocumentItem(documentItem: DocumentItem)
}