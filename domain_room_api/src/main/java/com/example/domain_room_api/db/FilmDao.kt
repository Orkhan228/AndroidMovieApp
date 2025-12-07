package com.example.domain_room_api.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.domain_room_api.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface FilmDao {

    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): Flowable<List<Film>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllToDb(films: List<Film>): Completable

    @Query("DELETE FROM cached_films")
    fun deleteAllFilmsFromDB(): Completable

}