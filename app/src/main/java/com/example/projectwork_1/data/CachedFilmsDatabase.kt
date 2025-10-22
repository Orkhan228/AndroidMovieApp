package com.example.projectwork_1.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projectwork_1.data.dao.FilmDao
import com.example.projectwork_1.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class CachedFilmsDatabase : RoomDatabase() {
    abstract fun filmsDao(): FilmDao
}