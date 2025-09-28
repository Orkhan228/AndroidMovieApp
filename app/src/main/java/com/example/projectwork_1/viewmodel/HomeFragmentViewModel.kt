package com.example.projectwork_1.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectwork_1.App
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.domain.Interactor

class HomeFragmentViewModel : ViewModel() {
    //Создаем здесь список, чтобы добавлять туда наши фильмы, для того чтобы наш список не менялся полностью, а лишь добавлялись новые фильмы
    private var allFilms = mutableListOf<Film>()
    private var currentPage = 1
    private var isLoading = false

    val filmsListLiveData = MutableLiveData<List<Film>>()
    private lateinit var interactor: Interactor
    init {
        interactor = App.instance.interactor
        //вызываем этот метод, для того чтобы запросить и отоброзить первую страницу
        loadPage(currentPage)
    }

    //метод для запроса с нужной странией
    private fun loadPage(page: Int) {
        //проверяем, если уже идет загрузка, то выходим из метода
        if (isLoading) return
        isLoading = true

        interactor.getFilmsFromApi(page, object : ApiCallBack {
            override fun onSuccess(films: List<Film>) {
                //добавляем сюда все и новые фильмы
                allFilms.addAll(films)
                //уведомляем подписчиков
                filmsListLiveData.postValue(allFilms)
                //прибавляем страницу
                currentPage++
                //уведомляем о том, что загрузка окончена
                isLoading = false
            }

            override fun onFailure() {
                isLoading = false
            }
        })

    }


    //публичный метод, который в параметры принимает текущую страницу
    fun loadNextPage() {
        loadPage(currentPage)
    }


    interface ApiCallBack {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }

}