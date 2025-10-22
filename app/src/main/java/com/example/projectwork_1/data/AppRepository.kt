package com.example.projectwork_1.data

import android.database.sqlite.SQLiteDatabase
import com.example.projectwork_1.data.entity.Film

interface AppRepository {
//    val sqlDb: SQLiteDatabase
    val favoriteFilms: MutableList<Film>

    fun putToDb(films: List<Film>)
    fun getAllFromDb(): List<Film>
    fun deleteFilmsFromDb(films: List<Film>)
//    fun updateDb(id: Int, film: Film)
//    fun deleteFilmFromDb(id: Int)
//    fun getWellRatedFilms(): List<Film>
}