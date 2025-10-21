package com.example.projectwork_1.data.db

import android.database.sqlite.SQLiteDatabase

interface AppDatabaseHelper {
    fun getReadable(): SQLiteDatabase
}