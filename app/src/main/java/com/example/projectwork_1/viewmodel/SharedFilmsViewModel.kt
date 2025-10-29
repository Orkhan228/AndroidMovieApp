package com.example.projectwork_1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectwork_1.App
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.domain.Interactor
import com.example.projectwork_1.utils.SingleLiveEvent
import java.util.concurrent.Executors
import javax.inject.Inject

class SharedFilmsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    //Settings ViewModel
    //создаем наблюдаемый список, который хранит категории
    val categoryPropertyLiveData: MutableLiveData<String> = MutableLiveData()

    //создаем наблюдаемый список, который хранит стили темы
    val themeLiveData: MutableLiveData<String> = MutableLiveData()

    //создаем livedata для показа progressBar
    val showProgressBar = MutableLiveData<Boolean>()

    val showErrorData = SingleLiveEvent<String>()

    private val tenMinutes = 10 * 60 * 1000L

    private var currentTime: Long = 0
    private var currentPage = 1
    private var isLoading = false
    //переменная - флаг, для того чтобы знать показывать все фильмы или нет
    private var showOnlyWellRated = false

    // Все фильмы
    val filmsListLiveData: LiveData<List<Film>>

    // Избранные фильмы
    private val favFilms = mutableListOf<Film>()
    val favFilmsLiveData = MutableLiveData<List<Film>>()

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDb()
        loadPage(currentPage)
        //вызываем метод, описанный ниже, чтобы положить значение в categoryPropertyLiveData, при создании экземпляра,
        //чтобы при первом запуске, были отмечены кнопки в SettingsFragment
        getCategoryProperty()
        //в этом блоке ставим значение в наш наблюдаемый список, чтобы при первом запуске была уже выбранная тема в радио кнопках
        themeLiveData.value = interactor.getTheme()
    }


    fun loadPage(page: Int) {
        if (isLoading) return
        isLoading = true
        showProgressBar.postValue(true)

        interactor.getFilmsFromApi(page, object : ApiCallBack {
            override fun onSuccess() {
                //сохраняем время последней успешной загрузки
                interactor.saveUpdateTime(System.currentTimeMillis())
                currentPage++
                isLoading = false
                showProgressBar.postValue(false)
                //println("!!! OnSuccess")
            }
            //в этом методе, когда у нас не работает сеть, выполняется код
            override fun onFailure() {
                //println("!!! problems with net, using database as cash")
                //когда, происходит ошибка в сети, выполняется этот метод
                showProgressBar.postValue(false)
                showErrorData.postValue("An error occurred, please check your connection!")
                filmsLogic()
            }
        })
    }

    fun loadNextPage() = loadPage(currentPage)

    //метод новый логики загрузки фильмов из бд
    fun filmsLogic() {
        //берем время последней успешной загрузки
        val lastUpdateTime = interactor.getLastUpdateTime()
        //записываем текущее время
        currentTime = System.currentTimeMillis()
        //println("!!! Films logic")

        //тут в условии проверяем, если разница меньше или равна 10 минутам, то запускаем отдельный поток, в котором используем данные
        //из бд и ставим их в наш обозреваемый список, а также меняем флаг isLoading на false
        if (currentTime - lastUpdateTime <= tenMinutes) {
            //println("!!! Using cached data (less than 10 minutes old)")
            Executors.newSingleThreadExecutor().execute {
                isLoading = false
            }
        }
        //если прошло больше 10 минут, то данные устарели, вызываем метод удаления записей из бд, очищаем список, уведомляем наш список
        //изменяем флаг isLoading на false, сбрасываем счетчик страниц, так как фильмы мы удалили и вызываем метод loadPage, который делает новый запрос
        else {
            Executors.newSingleThreadExecutor().execute {
                //println("!!! delete and refresh")
                interactor.deleteFilmsFromDB()
                isLoading = false
                currentPage = 1
                loadPage(currentPage)
            }
        }
    }

    fun addToFavorites(film: Film) {
        if (!favFilms.contains(film)) {
            favFilms.add(film)
            film.isInFavorites = true
            favFilmsLiveData.postValue(favFilms)
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
        fun onSuccess()
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
        interactor.deleteFilmsFromDB()
        currentPage = 1
        loadPage(currentPage)
    }

    //метод для изменения темы, также после изменения мы уведомляем наш наблюдаемый список
    fun setTheme(theme: String) {
        interactor.saveTheme(theme)
        themeLiveData.value = theme
    }


}