package com.example.projectwork_1.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectwork_1.data.entity.Film
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Flow<List<Film>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllToDb(films: List<Film>)

    @Query("DELETE FROM cached_films")
    fun deleteAllFilmsFromDB()

}