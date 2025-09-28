package com.example.projectwork_1.domain

import com.example.projectwork_1.utils.API
import com.example.projectwork_1.utils.TmdbApi
import com.example.projectwork_1.data.MainRepository
import com.example.projectwork_1.data.entity.TmdbResultsDTO
import com.example.projectwork_1.utils.Converter
import com.example.projectwork_1.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Interactor(val mainRepo: MainRepository, private val retrofitService: TmdbApi) {
    fun getFavFilmsDB() : List<Film> {
        return mainRepo.favoriteFilms
    }
    fun addFavFilmsToDB(film: Film) {
        mainRepo.favoriteFilms.add(film)
    }
    fun removeFavFilmsFromDB(film: Film) {
        mainRepo.favoriteFilms.remove(film)
    }

    fun getFilmsFromApi(page: Int, callBack: HomeFragmentViewModel.ApiCallBack) {
        retrofitService.getFilms(API.KEY, "ru-RU", page)
            .enqueue(object : Callback<TmdbResultsDTO> {
                override fun onResponse(
                    call: Call<TmdbResultsDTO?>,
                    response: Response<TmdbResultsDTO?>,
                ) {
                    callBack.onSuccess(Converter.convertApiListToDtoList(response.body()?.tmdbFilms))

                }

                override fun onFailure(
                    call: Call<TmdbResultsDTO?>,
                    t: Throwable,
                ) {
                    callBack.onFailure()

                }

            })
    }
}