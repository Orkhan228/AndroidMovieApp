package com.example.projectwork_1.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectwork_1.App
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.domain.Interactor
import com.example.projectwork_1.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Url
import java.net.URL
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SharedFilmsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    //Settings ViewModel
    //создаем наблюдаемый список, который хранит категории
    val categoryPropertyFlow = MutableStateFlow<String>("")

    //создаем наблюдаемый список, который хранит стили темы
    val themeFlowData = MutableStateFlow<String>("")

    //создаем livedata для показа progressBar
    val showProgressBarFlow = MutableStateFlow<Boolean>(false)

    val showErrorData = SingleLiveEvent<String>()

    private val tenMinutes = 10 * 60 * 1000L

    private var currentTime: Long = 0
    private var currentPage = 1
    private var isLoading = false
    private var lastLoadFailed = false

    // Все фильмы
    val filmsListFlowData: Flow<List<Film>>

    // Избранные фильмы
    private val favFilms = mutableListOf<Film>()
    val favFilmsFlowData = MutableStateFlow<List<Film>>(emptyList())

    init {
        App.instance.dagger.inject(this)
        filmsListFlowData = interactor.getFilmsFromDb()
        loadPage(currentPage)
        //вызываем метод, описанный ниже, чтобы положить значение в categoryPropertyLiveData, при создании экземпляра,
        //чтобы при первом запуске, были отмечены кнопки в SettingsFragment
        getCategoryProperty()
        //в этом блоке ставим значение в наш наблюдаемый список, чтобы при первом запуске была уже выбранная тема в радио кнопках
        themeFlowData.value = interactor.getTheme()
    }


    fun loadPage(page: Int) {
        if (isLoading) return
        isLoading = true

        if (!lastLoadFailed) {
            showProgressBarFlow.value = true
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                interactor.getFilmsFromApi(page, object : ApiCallBack {
                    override fun onSuccess() {
                        //сохраняем время последней успешной загрузки
                        interactor.saveUpdateTime(System.currentTimeMillis())
                        currentPage++
                        isLoading = false
                        lastLoadFailed = false
                        showProgressBarFlow.value = false
                    }
                    //в этом методе, когда у нас не работает сеть, выполняется код
                    override fun onFailure() {
                        //когда, происходит ошибка в сети, выполняется этот метод
                        showProgressBarFlow.value = false
                        if (!lastLoadFailed) {
                            showErrorData.postValue("An error occurred, please check your connection!")
                        }
                        lastLoadFailed = true
                        filmsLogic()
                    }
                })
            }
        }

    }

    fun loadNextPage() = loadPage(currentPage)

    //метод новый логики загрузки фильмов из бд
    fun filmsLogic() {
        //берем время последней успешной загрузки
        val lastUpdateTime = interactor.getLastUpdateTime()
        //записываем текущее время
        currentTime = System.currentTimeMillis()

        //тут в условии проверяем, если разница меньше или равна 10 минутам, то запускаем отдельный поток, в котором используем данные
        //из бд и ставим их в наш обозреваемый список, а также меняем флаг isLoading на false
        if (currentTime - lastUpdateTime <= tenMinutes) {
            isLoading = false
        }
        //если прошло больше 10 минут, то данные устарели, вызываем метод удаления записей из бд, очищаем список, уведомляем наш список
        //изменяем флаг isLoading на false, сбрасываем счетчик страниц, так как фильмы мы удалили и вызываем метод loadPage, который делает новый запрос
        else {
            viewModelScope.launch {
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
            favFilmsFlowData.value = favFilms
        }
    }

    fun removeFromFavorites(film: Film) {
        if (favFilms.contains(film)) {
            favFilms.remove(film)
            film.isInFavorites = false
            favFilmsFlowData.value = favFilms.toList()
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
        categoryPropertyFlow.value = interactor.getDefaultCategoryFromPreferences()
    }

    //метод, который меняет категорию, после изменения вызывает getCategoryProperty(), который уведомляет подписчиков, а после
    //очищает список всех фильмов, так как категория поменялась нам нужны совсем другие фильмы, также обновляем currentPage, чтобы
    //не сломать логику загрузок новых страниц.

    fun putCategoryProperty(category: String) {
        viewModelScope.launch {
            interactor.saveDefaultCategoryToPreferences(category)
            getCategoryProperty()
            interactor.deleteFilmsFromDB()
            currentPage = 1
            loadPage(currentPage)
        }
    }

    //метод для изменения темы, также после изменения мы уведомляем наш наблюдаемый список
    fun setTheme(theme: String) {
        interactor.saveTheme(theme)
        themeFlowData.value = theme
    }


    //DetailsFragment
    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            try {
                val url = URL(url)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().inputStream)
                if (bitmap != null) {
                    it.resume(bitmap)
                }
                else {
                    it.resumeWithException(Exception("Failed to load image!"))
                }
            } catch (e: Exception) {
                it.resumeWithException(e)
            }

        }
    }

}