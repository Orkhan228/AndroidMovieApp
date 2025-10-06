//package com.example.projectwork_1.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.projectwork_1.domain.AppInteractor
//import com.example.projectwork_1.domain.Film
//
//import javax.inject.Inject
//
//
//class DetailsFragmentViewModel @Inject constructor(private val interactor: AppInteractor) : ViewModel() {
//    val favFilmsLiveData = MutableLiveData<List<Film>>()
//    init {
//        val favFilms = interactor.getFavFilmsDB()
//        favFilmsLiveData.postValue(favFilms)
//    }
//
//    fun addToFavorites(film: Film) {
//        interactor.addFavFilmsToDB(film)
//        val items = interactor.getFavFilmsDB()
//        favFilmsLiveData.postValue(items)
//    }
//
//    fun removeFromFavorites(film: Film) {
//        interactor.removeFavFilmsFromDB(film)
//        val items = interactor.getFavFilmsDB()
//        favFilmsLiveData.postValue(items)
//    }
//}