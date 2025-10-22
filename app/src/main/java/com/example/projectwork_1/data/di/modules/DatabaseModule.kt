package com.example.projectwork_1.data.di.modules

import android.content.Context
import androidx.room.Room
import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.CachedFilmsDatabase
import com.example.projectwork_1.data.MainRepository
import com.example.projectwork_1.data.db.AppDatabaseHelper
import com.example.projectwork_1.data.db.DatabaseHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    fun provideFilmDao(context: Context) =
        Room.databaseBuilder(
            context,
            CachedFilmsDatabase::class.java,
            "cached_films_db")
            .build().filmsDao()

    @Module
    interface BindModule {
        @Binds
        @Singleton
        fun bindMainRepository(mainRepository: MainRepository): AppRepository

        @Binds
        @Singleton
        fun bindDbHelper(dbHelper: DatabaseHelper): AppDatabaseHelper
    }
}