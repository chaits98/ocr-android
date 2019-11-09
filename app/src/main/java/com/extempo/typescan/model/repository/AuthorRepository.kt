package com.extempo.typescan.model.repository

import android.app.Activity
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.database.AuthorDatabaseRepository

class AuthorRepository(private val activity: Activity) {

    fun getAllAuthors(): List<Author> {
        return AuthorDatabaseRepository.getAllAuthors(activity)
    }

    fun insertAuthor(author: Author) {
        AuthorDatabaseRepository.insertAuthor(author, activity)
    }

    fun deleteAuthor(author: Author) {
        AuthorDatabaseRepository.deleteAuthor(author, activity)
    }

}