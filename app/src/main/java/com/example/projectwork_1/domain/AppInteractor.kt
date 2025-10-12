package com.example.projectwork_1.domain

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
}