package com.example.projectwork_1.domain

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import kotlinx.coroutines.flow.Flow

interface AppInteractor {
    fun getFavFilmsDB() : List<Film>
    fun addFavFilmsToDB(film: Film)
    fun removeFavFilmsFromDB(film: Film)
    suspend fun getFilmsFromApi(page: Int, callBack: SharedFilmsViewModel.ApiCallBack)
    fun saveDefaultCategoryToPreferences(category: String)
    fun getDefaultCategoryFromPreferences(): String
    fun saveTheme(theme: String)
    fun getTheme(): String
    fun getFilmsFromDb(): Flow<List<Film>>
    fun saveUpdateTime(time: Long)
    fun getLastUpdateTime(): Long
    suspend fun deleteFilmsFromDB()
}