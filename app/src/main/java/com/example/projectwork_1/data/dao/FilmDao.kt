package com.example.projectwork_1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectwork_1.data.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {

    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Flowable<List<Film>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllToDb(films: List<Film>): Completable

    @Query("DELETE FROM cached_films")
    fun deleteAllFilmsFromDB(): Completable

}