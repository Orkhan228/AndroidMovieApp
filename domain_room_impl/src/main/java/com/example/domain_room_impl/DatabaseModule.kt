package com.example.domain_room_impl

import android.content.Context
import androidx.room.Room
import com.example.domain_room_api.db.DatabaseContract
import com.example.domain_room_api.db.FilmDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFilmDao(dbContract: DatabaseContract): FilmDao {
        return dbContract.contractFilmDAO()
    }

    @Provides
    @Singleton
    fun provideFilmsDatabase(context: Context): DatabaseContract {
        return Room.databaseBuilder(
            context,
            CachedFilmsDatabase::class.java,
            "cached_films_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}