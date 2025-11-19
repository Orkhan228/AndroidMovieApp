package com.example.projectwork_1.data

import com.example.projectwork_1.data.dao.FilmDao
import com.example.projectwork_1.data.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(private val filmDao: FilmDao) : AppRepository {

    override val favoriteFilms = mutableListOf<Film>()

    override fun putToDb(list: List<Film>): Completable = filmDao.insertAllToDb(list)

    override fun deleteFilmsFromDb(): Completable = filmDao.deleteAllFilmsFromDB()

    override fun getAllFromDb(): Flowable<List<Film>> = filmDao.getCachedFilms()
}