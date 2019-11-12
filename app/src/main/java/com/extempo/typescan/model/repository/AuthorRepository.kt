package com.extempo.typescan.model.repository

import android.content.Context
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.database.AuthorDatabaseRepository

class AuthorRepository(private val context: Context) {

    fun getAllAuthors(): List<Author> {
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