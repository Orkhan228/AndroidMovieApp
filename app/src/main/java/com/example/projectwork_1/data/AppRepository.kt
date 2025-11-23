package com.example.projectwork_1.data

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    val favoriteFilms: MutableList<Film>

    fun getAllFromDb(): Flowable<List<Film>>
    fun putToDb(films: List<Film>): Completable
    fun deleteFilmsFromDb(): Completable
}