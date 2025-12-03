package com.example.projectwork_1.data

import com.example.domain_room_api.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface AppRepository {
    val favoriteFilms: MutableList<Film>

    fun getAllFromDb(): Flowable<List<Film>>
    fun putToDb(films: List<Film>): Completable
    fun deleteFilmsFromDb(): Completable
}