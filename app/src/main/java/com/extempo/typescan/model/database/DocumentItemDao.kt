package com.extempo.typescan.model.database

import androidx.room.*
import com.extempo.typescan.model.DocumentItem

@Dao
interface DocumentItemDao {
    @Query("SELECT * FROM documents")
    fun getAllDocumentItems(): List<DocumentItem>

//    @Query("SELECT * FROM documents WHERE id = :docId")
//    fun getDocumentItemById(docId: Long): List<DocumentItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(documentItem: DocumentItem)

    @Delete
    fun deleteDocumentItem(documentItem: DocumentItem)

    @Query("DELETE FROM documents")
    fun deleteAllDocumentItems()

    @Update
    fun updateDocumentItem(documentItem: DocumentItem)
}