package com.example.projectwork_1.data

import android.database.sqlite.SQLiteDatabase
import com.example.projectwork_1.domain.Film

interface AppRepository {
    val sqlDb: SQLiteDatabase
    val favoriteFilms: MutableList<Film>

    fun putToDb(film: Film)
    fun getAllFromDb(): List<Film>
    fun updateDb(id: Int, film: Film)
    fun deleteFilmFromDb(id: Int)
    fun getWellRatedFilms(): List<Film>
}