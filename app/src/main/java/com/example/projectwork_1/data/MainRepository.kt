package com.example.projectwork_1.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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

    override fun getAllFromDb(): List<Film> = filmDao.getCachedFilms()

    override fun deleteFilmsFromDb(films: List<Film>) {
        Executors.newSingleThreadExecutor().execute {
            filmDao.deleteAllFilmsFromDB(films)
        }
    }


    //    private lateinit var cursor: Cursor
//    private lateinit var wellCursor: Cursor


    //метод для того, чтобы вставить фильм в БД
//    override fun putToDb(film: Film) {
//        val cv = ContentValues()
//        cv.apply {
//            put(DatabaseHelper.COLUMN_TITLE, film.title)
//            put(DatabaseHelper.COLUMN_POSTER, film.poster)
//            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
//            put(DatabaseHelper.COLUMN_RATING, film.rating)
//        }
//        sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
//    }
//
//    //метод, который берет все записи из БД
//    override fun getAllFromDb(): List<Film> {
//        //тут мы выбираем всю таблицу
//        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
//
//        val result = mutableListOf<Film>()
//        //если курсор может перейти на первую запись, то код выполняется
//        if (cursor.moveToFirst()) {
//            //используем do while, в while говорим, если курсор может двигаться дальше, то выполняем код в do
//            do {
//                val title = cursor.getString(1)
//                val poster = cursor.getString(2)
//                val desc = cursor.getString(3)
//                val rating = cursor.getDouble(4)
//
//                result.add(Film(title, poster, desc, rating))
//            } while (cursor.moveToNext())
//        }
//
//        return result
//    }
//
//    override fun updateDb(id: Int, film: Film) {
//        val cv = ContentValues()
//        cv.apply {
//            put(DatabaseHelper.COLUMN_TITLE, film.title)
//            put(DatabaseHelper.COLUMN_POSTER, film.poster)
//            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
//            put(DatabaseHelper.COLUMN_RATING, film.rating)
//        }
//        sqlDb.update(DatabaseHelper.TABLE_NAME, cv, DatabaseHelper.COLUMN_ID + "=" + "?", arrayOf("$id"))
//    }
//
//    override fun deleteFilmFromDb(id: Int) {
//        sqlDb.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=" + "?", arrayOf("$id"))
//    }

//    override fun getWellRatedFilms(): List<Film> {
//        val result = mutableListOf<Film>()
//        val wellRating = 8.5
//        wellCursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_RATING} >= $wellRating", null)
//        if (wellCursor.moveToFirst()) {
//            do {
//                val title = wellCursor.getString(1)
//                val poster = wellCursor.getString(2)
//                val desc = wellCursor.getString(3)
//                val rating = wellCursor.getDouble(4)
//
//                result.add(Film(title, poster, desc, rating))
//            } while (wellCursor.moveToNext())
//        }
//        return result
//    }
}