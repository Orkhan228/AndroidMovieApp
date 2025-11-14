package com.example.projectwork_1.data

import com.example.projectwork_1.data.dao.FilmDao
import com.example.projectwork_1.data.entity.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(private val filmDao: FilmDao) : AppRepository {

    override val favoriteFilms = mutableListOf<Film>()

    override suspend fun putToDb(list: List<Film>) {
        withContext(Dispatchers.IO) {
            filmDao.insertAllToDb(list)
        }
    }

    override suspend fun deleteFilmsFromDb() {
        withContext(Dispatchers.IO) {
            filmDao.deleteAllFilmsFromDB()
        }
    }

    override fun getAllFromDb(): Flow<List<Film>> = filmDao.getCachedFilms()
}