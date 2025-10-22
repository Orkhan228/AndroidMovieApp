package com.example.projectwork_1.domain

import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.utils.API
import com.example.projectwork_1.data.entity.TmdbResultsDTO
import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import com.example.projectwork_1.utils.AppTmdbApi
import com.example.projectwork_1.utils.Converter
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
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

    override fun getFilmsFromApi(page: Int, callBack: SharedFilmsViewModel.ApiCallBack) {
        //в метод getFilms, необходимо добавить категорию, которую мы добавляем методом getDefaultCategoryFromPreferences(),
        //который описан ниже
        retrofitService.api.getFilms(getDefaultCategoryFromPreferences(), API.KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResultsDTO> {
                override fun onResponse(
                    call: Call<TmdbResultsDTO?>,
                    response: Response<TmdbResultsDTO?>,
                ) {
                    val list = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)
                    mainRepo.putToDb(list)
                    callBack.onSuccess(list)
                }

                override fun onFailure(
                    call: Call<TmdbResultsDTO?>,
                    t: Throwable,
                ) {
                    callBack.onFailure()
                }

            })
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
    override fun getFilmsFromDb(): List<Film> = mainRepo.getAllFromDb()

    override fun saveUpdateTime(time: Long) {
        preference.saveUpdateTime(time)
    }

    override fun getLastUpdateTime(): Long = preference.getLastUpdateTime()

    override fun deleteFilmsFromDB(films: List<Film>) {
        mainRepo.deleteFilmsFromDb(films)
    }

    //метод для обновления базы данных по айди и фильму, фильм нужен для передачи нового измененного фильма
//    override fun updateDb(id: Int, film: Film) {
//        mainRepo.updateDb(id, film)
//    }

    //удаляет фильм из БД по айди
//    override fun deleteFilmFromDb(id: Int) {
//        mainRepo.deleteFilmFromDb(id)
//    }

//    //выдает список фильмов с высокоим рейтингом
//    override fun getWellRatedFilmsFromDb(): List<Film> = mainRepo.getWellRatedFilms()
}