package com.extempo.typescan.model.database

import android.app.Activity
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.extempo.typescan.model.Author
import com.extempo.typescan.utilities.ThreadManagement

object AuthorDatabaseRepository {

    @WorkerThread
    fun getAllAuthors(context: Context): LiveData<List<Author>> {
        return AuthorDatabase.getInstance(context).authorDao().getAllAuthors()
    }

    @WorkerThread
    fun insertAuthor(author: Author, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            println("log_tag: repo: " + author)
            AuthorDatabase.getInstance(context).authorDao().insertAuthor(author)
        }
    }

    @WorkerThread
    fun deleteAuthor(author: Author, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            AuthorDatabase.getInstance(context).authorDao().deleteAuthor(author)
        }
    }

    @WorkerThread
    fun updateAuthor(author: Author, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            AuthorDatabase.getInstance(context).authorDao().updateAuthor(author)
        }
    }

}