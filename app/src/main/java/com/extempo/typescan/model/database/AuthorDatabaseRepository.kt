package com.extempo.typescan.model.database

import android.app.Activity
import android.content.Context
import androidx.annotation.WorkerThread
import com.extempo.typescan.model.Author
import com.extempo.typescan.utilities.ThreadManagement

object AuthorDatabaseRepository {

    @WorkerThread
    fun getAllAuthors(context: Context): List<Author> {
        return AuthorDatabase.getInstance(context).authorDao().getAllAuthors()
    }

    @WorkerThread
    fun insertAuthor(author: Author, activity: Activity) {
        ThreadManagement.databaseExecutor.execute {
            author.initCharMap(activity)
            AuthorDatabase.getInstance(activity).authorDao().insertAuthor(author)
        }
    }

    @WorkerThread
    fun deleteAuthor(author: Author, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            AuthorDatabase.getInstance(context).authorDao().deleteAuthor(author)
        }
    }

}