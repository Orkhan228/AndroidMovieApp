package com.example.projectwork_1.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.dao.FilmDao
import com.example.projectwork_1.data.db.AppDatabaseHelper
import com.example.projectwork_1.data.db.DatabaseHelper
import com.example.projectwork_1.data.entity.Film
import java.util.concurrent.Executors
import javax.inject.Inject

class MainRepository @Inject constructor(private val filmDao: FilmDao) : AppRepository {

    override val favoriteFilms = mutableListOf<Film>()

    override fun putToDb(list: List<Film>) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAllToDb(list)
        }
    }

    override fun getAllFromDb(): LiveData<List<Film>> = filmDao.getCachedFilms()

    override fun deleteFilmsFromDb() {
        Executors.newSingleThreadExecutor().execute {
            filmDao.deleteAllFilmsFromDB()
        }
    }
}