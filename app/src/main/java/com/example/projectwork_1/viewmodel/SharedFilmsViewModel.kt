package com.example.projectwork_1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectwork_1.App
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.domain.Interactor
import javax.inject.Inject

class SharedFilmsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    // Все фильмы
    private val allFilms = mutableListOf<Film>()
    val filmsListLiveData = MutableLiveData<List<Film>>()

    // Избранные фильмы
    private val favFilms = mutableListOf<Film>()
    val favFilmsLiveData = MutableLiveData<List<Film>>()

    private var currentPage = 1
    private var isLoading = false

    init {
        App.instance.dagger.inject(this)
        loadPage(currentPage)
    }

    fun loadPage(page: Int) {
        if (isLoading) return
        isLoading = true

        interactor.getFilmsFromApi(page, object : ApiCallBack {
            override fun onSuccess(films: List<Film>) {
                allFilms.addAll(films)
                filmsListLiveData.postValue(allFilms.toList())
                currentPage++
                isLoading = false
            }

            override fun onFailure() {
                isLoading = false
            }
        })
    }

    fun loadNextPage() = loadPage(currentPage)

    fun addToFavorites(film: Film) {
        if (!favFilms.contains(film)) {
            favFilms.add(film)
            film.isInFavorites = true
            favFilmsLiveData.postValue(favFilms.toList())
        }
    }

    fun removeFromFavorites(film: Film) {
        if (favFilms.contains(film)) {
            favFilms.remove(film)
            film.isInFavorites = false
            favFilmsLiveData.postValue(favFilms.toList())
        }
    }

    interface ApiCallBack {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}