package com.extempo.typescan.model.repository

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.database.AuthorDatabaseRepository
import java.math.MathContext

class AuthorRepository(private val context: Context) {

    fun getAllAuthors(): LiveData<List<Author>> {
        return AuthorDatabaseRepository.getAllAuthors(context)
    }

    fun insertAuthor(author: Author) {
        AuthorDatabaseRepository.insertAuthor(author, context)
    }

    fun deleteAuthor(author: Author) {
        AuthorDatabaseRepository.deleteAuthor(author, context)
    }

    fun updateAuthor(author: Author) {
        AuthorDatabaseRepository.updateAuthor(author, context)
    }
}