package com.example.projectwork_1.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.App
import com.example.projectwork_1.domain.Interactor
import com.example.projectwork_1.entity.WatchLaterNotification
import com.example.projectwork_1.utils.SingleLiveEvent
import com.example.projectwork_1.utils.WatchLaterFilmDiffUtil
import com.example.projectwork_1.view.rv_adapters.WatchLaterFilmsAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SharedFilmsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    val compositeDisposable = CompositeDisposable()

    val searchSubject = PublishSubject.create<String>()

    //Settings ViewModel
    //создаем наблюдаемый список, который хранит категории
    val categoryPropertyFlow = PublishSubject.create<String>()

    //создаем наблюдаемый список, который хранит стили темы
    val themeFlowData = PublishSubject.create<String>()

    //создаем livedata для показа progressBar
    val showProgressBarFlow = PublishSubject.create<Boolean>()

    val showErrorData = SingleLiveEvent<String>()

    private val tenMinutes = 10 * 60 * 1000L

    private var currentTime: Long = 0
    private var currentPage = 1
    private var searchCurrentPage = 1
    private var isLoading = false
    private var lastLoadFailed = false
    private var currentQuery = ""

    // Все фильмы
    val filmsListFlowableData: Flowable<List<Film>>

    // Избранные фильмы
    private val favFilms = mutableListOf<Film>()
    val favFilmsFlowData = BehaviorSubject.create<List<Film>>()

    // Фильмы добавленные в Смотреть позже
    private var watchLaterFilms = mutableListOf<WatchLaterNotification>()
    val watchLaterFilmsFlowData = BehaviorSubject.create<List<WatchLaterNotification>>()

    init {
        App.instance.getApp().inject(this)
        filmsListFlowableData = interactor.getFilmsFromDb()
        loadPage(currentPage)
        //вызываем метод, описанный ниже, чтобы положить значение в categoryPropertyLiveData, при создании экземпляра,
        //чтобы при первом запуске, были отмечены кнопки в SettingsFragment
        getCategoryProperty()
        //в этом блоке ставим значение в наш наблюдаемый список, чтобы при первом запуске была уже выбранная тема в радио кнопках
        themeFlowData.onNext(interactor.getTheme())
    }


    fun loadPage(page: Int) {
        if (isLoading) return
        isLoading = true

        if (!lastLoadFailed) {
            showProgressBarFlow.onNext(true)
        }

        compositeDisposable.add(
            interactor.getFilmsFromApi(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        //сохраняем время последней успешной загрузки
                        interactor.saveUpdateTime(System.currentTimeMillis())
                        currentPage++
                        isLoading = false
                        lastLoadFailed = false
                        showProgressBarFlow.onNext(false)
                    },
                    {
                        //когда, происходит ошибка в сети, выполняется этот метод
                        showProgressBarFlow.onNext(false)
                        if (!lastLoadFailed) {
                            showErrorData.postValue("An error occurred, please check your connection!")
                        }
                        lastLoadFailed = true
                        filmsLogic()
                    }
                )
        )

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


    fun searchFilm(
        page: Int = 1,
        includeAdult: Boolean = false,
    ): Observable<List<Film>> {
        return searchSubject
            .subscribeOn(Schedulers.io())
            .debounce(350L, TimeUnit.MILLISECONDS)
            .switchMapSingle { query ->
                currentQuery = query
                searchCurrentPage = 1
                interactor.searchFilm(query.lowercase(), searchCurrentPage, includeAdult)
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchNextFilm(includeAdult: Boolean = false): Single<List<Film>> {
        searchCurrentPage++
        return interactor.searchFilm(currentQuery.lowercase(), searchCurrentPage, includeAdult)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }


    fun addToFavorites(film: Film) {
        if (!favFilms.contains(film)) {
            favFilms.add(film)
            film.isInFavorites = true
            favFilmsFlowData.onNext(favFilms)
        }
    }

    fun removeFromFavorites(film: Film) {
        if (favFilms.contains(film)) {
            favFilms.remove(film)
            film.isInFavorites = false
            favFilmsFlowData.onNext(favFilms.toList())
        }
    }

    fun addToWatchLater(film: Film, triggerTime: Long) {
        if (!watchLaterFilms.any {it.film.id == film.id}) {
            watchLaterFilms.add(
                WatchLaterNotification(
                    film,
                    triggerTime,
                    film.id
                )
            )
            film.isInWatchLater = true

            watchLaterFilmsFlowData.onNext(watchLaterFilms.toList())
        }
    }

    fun removeFromWatchLater(filmId: Int) {
        watchLaterFilms.find { it.film.id == filmId }?.film?.isInWatchLater = false
        watchLaterFilms.removeAll { it.film.id == filmId }

        watchLaterFilmsFlowData.onNext(watchLaterFilms)
    }

    fun updateWatchLater(updated: WatchLaterNotification) {
        val index = watchLaterFilms.indexOfFirst {
            it.film.id == updated.film.id
        }
        if (index == -1) return

        watchLaterFilms[index] = updated
        watchLaterFilmsFlowData.onNext(watchLaterFilms.toList())
    }





    //интерфейс для метода getFilmsFromApi() из интерактора, там мы передаем список фильмов, а тут мы пишем реализацию этого интерфейса
    interface ApiCallBack {
        fun onSuccess()
        fun onFailure()
    }

    //Settings ViewModel
    //метод, который изменяет значения нашего наблюдаемого списка
    private fun getCategoryProperty() {
        categoryPropertyFlow.onNext(interactor.getDefaultCategoryFromPreferences())
    }

    //метод, который меняет категорию, после изменения вызывает getCategoryProperty(), который уведомляет подписчиков, а после
    //очищает список всех фильмов, так как категория поменялась нам нужны совсем другие фильмы, также обновляем currentPage, чтобы
    //не сломать логику загрузок новых страниц.

    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
        getCategoryProperty()
        compositeDisposable.add(
            interactor.deleteFilmsFromDB()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        currentPage = 1
                        loadPage(currentPage)
                    },
                    { e -> println("!!! Problem with CategoryProperty $e")}
                )
        )

    }

    //метод для изменения темы, также после изменения мы уведомляем наш наблюдаемый список
    fun setTheme(theme: String) {
        interactor.saveTheme(theme)
        themeFlowData.onNext(theme)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}