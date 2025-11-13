package com.example.projectwork_1.domain

import androidx.lifecycle.LiveData
import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.utils.API
import com.example.projectwork_1.data.entity.TmdbResultsDTO
import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import com.example.projectwork_1.utils.AppTmdbApi
import com.example.projectwork_1.utils.Converter
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
//передаем в конструктор объект нашего класса PreferenceProvider
//еще до этого, мы поменяли интерфейс TmdpApi, а именно изменили запрос getFilms, добавили туда @Path category
class Interactor @Inject constructor(val mainRepo: AppRepository, private val retrofitService: AppTmdbApi, private val preference: AppPreferenceProvider) : AppInteractor {
    override fun getFavFilmsDB() : List<Film> {
        return mainRepo.favoriteFilms
    }
    override fun addFavFilmsToDB(film: Film) {
        mainRepo.favoriteFilms.add(film)
    }
    override fun removeFavFilmsFromDB(film: Film) {
        mainRepo.favoriteFilms.remove(film)
    }

    override suspend fun getFilmsFromApi(page: Int, callBack: SharedFilmsViewModel.ApiCallBack) {
        //в метод getFilms, необходимо добавить категорию, которую мы добавляем методом getDefaultCategoryFromPreferences(),
        //который описан ниже
        val response = retrofitService.api.getFilms(
            getDefaultCategoryFromPreferences(),
            API.KEY,
            "ru-RU",
            page
        )
        if (response.isSuccessful) {
            val list = response.body()?.tmdbFilms?.map {
                Film(
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                    isInFavorites = false
                )
            }
            callBack.onSuccess()
            withContext(Dispatchers.IO) {
                mainRepo.putToDb(list.orEmpty())
            }
            delay(400)
        } else {
            callBack.onFailure()
            delay(400)
        }
    }

    //Метод чтобы сохранить категорию
    override fun saveDefaultCategoryToPreferences(category: String) {
        preference.saveDefaultCategory(category)
    }

    //метод чтобы получить категорию
    override fun getDefaultCategoryFromPreferences() = preference.getDefaultCategory()

    //метод для сохранения темы в SharedPreferences
    override fun saveTheme(theme: String) {
        preference.saveTheme(theme)
    }

    //метод для взятия темы из SharedPreferences
    override fun getTheme(): String = preference.getTheme()

    //методы для работы с базой данных
    override fun getFilmsFromDb(): Flow<List<Film>> = mainRepo.getAllFromDb()

    override fun saveUpdateTime(time: Long) {
        preference.saveUpdateTime(time)
    }

    override fun getLastUpdateTime(): Long = preference.getLastUpdateTime()

    override suspend fun deleteFilmsFromDB() {
        withContext(Dispatchers.IO) {
            mainRepo.deleteFilmsFromDb()
        }
    }

}