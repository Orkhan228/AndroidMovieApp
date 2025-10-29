package com.example.projectwork_1.data

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.entity.Film

interface AppRepository {
    val favoriteFilms: MutableList<Film>

    fun putToDb(films: List<Film>)
    fun getAllFromDb(): LiveData<List<Film>>
    fun deleteFilmsFromDb()
}