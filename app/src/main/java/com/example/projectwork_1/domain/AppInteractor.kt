package com.example.projectwork_1.domain

import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel

interface AppInteractor {
    fun getFavFilmsDB() : List<Film>
    fun addFavFilmsToDB(film: Film)
    fun removeFavFilmsFromDB(film: Film)
    fun getFilmsFromApi(page: Int, callBack: SharedFilmsViewModel.ApiCallBack)
    fun saveDefaultCategoryToPreferences(category: String)
    fun getDefaultCategoryFromPreferences(): String
    fun saveTheme(theme: String)
    fun getTheme(): String
    fun getFilmsFromDb(): List<Film>
    fun saveUpdateTime(time: Long)
    fun getLastUpdateTime(): Long
    fun deleteFilmsFromDB(films: List<Film>)
//    fun updateDb(id: Int, film: Film)
//    fun deleteFilmFromDb(id: Int)
//    fun getWellRatedFilmsFromDb(): List<Film>
}