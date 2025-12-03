package com.example.projectwork_1.domain

import com.example.domain_api.retrofit.TmdbApi
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.utils.API
import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

import javax.inject.Inject
//передаем в конструктор объект нашего класса PreferenceProvider
//еще до этого, мы поменяли интерфейс TmdpApi, а именно изменили запрос getFilms, добавили туда @Path category
class Interactor @Inject constructor(val mainRepo: AppRepository, private val retrofitService: TmdbApi, private val preference: AppPreferenceProvider) : AppInteractor {
    override fun getFavFilmsDB(): List<Film> {
        return mainRepo.favoriteFilms
    }

    override fun addFavFilmsToDB(film: Film) {
        mainRepo.favoriteFilms.add(film)
    }

    override fun removeFavFilmsFromDB(film: Film) {
        mainRepo.favoriteFilms.remove(film)
    }

    override fun getFilmsFromApi(page: Int): Completable {
        //в метод getFilms, необходимо добавить категорию, которую мы добавляем методом getDefaultCategoryFromPreferences(),
        //который описан ниже
        return retrofitService.getFilms(
            getDefaultCategoryFromPreferences(),
            API.KEY,
            "ru-RU",
            page
        ).flatMapCompletable { response ->
            val list = response.tmdbFilms.map {
                Film(
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                    isInFavorites = false
                )
            }

            mainRepo.putToDb(list)
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
    override fun getFilmsFromDb(): Flowable<List<Film>> = mainRepo.getAllFromDb()

    override fun saveUpdateTime(time: Long) {
        preference.saveUpdateTime(time)
    }

    override fun getLastUpdateTime(): Long = preference.getLastUpdateTime()

    override fun deleteFilmsFromDB() = mainRepo.deleteFilmsFromDb()


    override fun searchFilm(query: String, page: Int, includeAdult: Boolean): Single<List<Film>> {
        return retrofitService.searchFilm(query, page, API.KEY, "ru-RU", includeAdult)
            .map { result ->
                val list = result.tmdbFilms.map {
                    Film(
                        title = it.title,
                        poster = it.posterPath,
                        description = it.overview,
                        rating = it.voteAverage,
                        isInFavorites = false
                    )
                }
                list
            }
        }
}