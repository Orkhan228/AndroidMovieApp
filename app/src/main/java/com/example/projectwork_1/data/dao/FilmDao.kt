package com.example.projectwork_1.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectwork_1.data.entity.Film

@Dao
interface FilmDao {

    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllToDb(films: List<Film>)

    @Delete
    fun deleteAllFilmsFromDB(films: List<Film>)

}