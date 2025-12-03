package com.example.domain_room_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.domain_room_api.db.DatabaseContract
import com.example.domain_room_api.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = true)
abstract class CachedFilmsDatabase : RoomDatabase(), DatabaseContract
