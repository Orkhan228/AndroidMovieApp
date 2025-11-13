package com.example.projectwork_1.data

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.entity.Film
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    val favoriteFilms: MutableList<Film>

    suspend fun putToDb(films: List<Film>)
    fun getAllFromDb(): Flow<List<Film>>
    suspend fun deleteFilmsFromDb()
}