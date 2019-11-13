package com.extempo.typescan.model.database

import androidx.room.*
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.utilities.Converters

@Dao
@TypeConverters(Converters::class)
interface AuthorDao {

    @Query("SELECT * FROM Authors")
    fun getAllAuthors(): List<Author>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAuthor(author: Author)

    @Delete
    fun deleteAuthor(author: Author)

    @Update
    fun updateAuthor(author: Author)
}