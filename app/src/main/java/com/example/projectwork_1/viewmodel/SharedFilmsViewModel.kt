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

    //Settings ViewModel
    //создаем наблюдаемый список, который хранит категории
    val categoryPropertyLiveData: MutableLiveData<String> = MutableLiveData()

    //создаем наблюдаемый список, который хранит стили темы
    val themeLiveData: MutableLiveData<String> = MutableLiveData()

    //новая страница, при изменении категории
    private val newPage = 1

    private var currentPage = 1
    private var isLoading = false
    //переменная - флаг, для того чтобы знать показывать все фильмы или нет
    private var showOnlyWellRated = false

    init {
        App.instance.dagger.inject(this)
        loadPage(currentPage)
        //вызываем метод, описанный ниже, чтобы положить значение в categoryPropertyLiveData, при создании экземпляра,
        //чтобы при первом запуске, были отмечены кнопки в SettingsFragment
        getCategoryProperty()
        //в этом блоке ставим значение в наш наблюдаемый список, чтобы при первом запуске была уже выбранная тема в радио кнопках
        themeLiveData.value = interactor.getTheme()
    }

    // Все фильмы
    private val allFilms = mutableListOf<Film>()
    val filmsListLiveData = MutableLiveData<List<Film>>()

    // Избранные фильмы
    private val favFilms = mutableListOf<Film>()
    val favFilmsLiveData = MutableLiveData<List<Film>>()


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
            //в этом методе, когда у нас не работает сеть, выполняется код
            override fun onFailure() {
                val films = if (showOnlyWellRated) {
                    interactor.getWellRatedFilmsFromDb()
                } else {
                    interactor.getFilmsFromDb()
                }
                filmsListLiveData.postValue(films)

                //Логирование для проверки работы базы данных из кэша
                //println("!!! Using database as cash")
                showOnlyWellRated = false
                isLoading = false
            }
        })
    }

    fun getFilms() {
        interactor.getFilmsFromApi(newPage, object : ApiCallBack {
            override fun onSuccess(films: List<Film>) {
                //добавляем ко всем фильмам, чтобы загрузка была как положенная, то есть не просто список заменялся, а добавлялись новые фильмы
                allFilms.addAll(films)
                filmsListLiveData.postValue(allFilms.toList())
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

    //интерфейс для метода getFilmsFromApi() из интерактора, там мы передаем список фильмов, а тут мы пишем реализацию этого интерфейса
    interface ApiCallBack {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }

    //Settings ViewModel
    //метод, который изменяет значения нашего наблюдаемого списка
    private fun getCategoryProperty() {
        categoryPropertyLiveData.postValue(interactor.getDefaultCategoryFromPreferences())
    }

    //метод, который меняет категорию, после изменения вызывает getCategoryProperty(), который уведомляет подписчиков, а после
    //очищает список всех фильмов, так как категория поменялась нам нужны совсем другие фильмы, также обновляем currentPage, чтобы
    //не сломать логику загрузок новых страниц.
    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
        getCategoryProperty()
        allFilms.clear()
        currentPage = 1
    }

    //метод для изменения темы, также после изменения мы уведомляем наш наблюдаемый список
    fun setTheme(theme: String) {
        interactor.saveTheme(theme)
        themeLiveData.value = theme
    }

    //Дополнительные методы для взаимодействия с БД
    fun getWellRatedFilmsFromDb(): List<Film> = interactor.getWellRatedFilmsFromDb()
    //метод для показа фильмов с высоким рейтингом, сначала очищаем все фильмы, потом получаем фильмы с высоким рейтингом, добавляем
    //фильмы с высоким рейтингом в allFilms и уведомляем подписчиков об этом
    fun showWellRatedFilmsFromDb() {
        allFilms.clear()
        val filtered = getWellRatedFilmsFromDb()
        allFilms.addAll(filtered)
        filmsListLiveData.postValue(filtered)
    }

    //метод для изменения значения флага
    fun setShowOnlyWellRated(enabled: Boolean) {
        showOnlyWellRated = enabled
    }
}