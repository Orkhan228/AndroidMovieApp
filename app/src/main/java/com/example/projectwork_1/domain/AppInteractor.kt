package com.example.projectwork_1.domain

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface AppInteractor {
    fun getFavFilmsDB() : List<Film>
    fun addFavFilmsToDB(film: Film)
    fun removeFavFilmsFromDB(film: Film)
    fun getFilmsFromApi(page: Int): Completable
    fun saveDefaultCategoryToPreferences(category: String)
    fun getDefaultCategoryFromPreferences(): String
    fun saveTheme(theme: String)
    fun getTheme(): String
    fun getFilmsFromDb(): Flowable<List<Film>>
    fun saveUpdateTime(time: Long)
    fun getLastUpdateTime(): Long
    fun deleteFilmsFromDB(): Completable
    fun searchFilm(query: String, page: Int, includeAdult: Boolean): Single<List<Film>>
}