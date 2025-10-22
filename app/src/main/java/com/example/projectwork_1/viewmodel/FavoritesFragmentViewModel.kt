//package com.example.projectwork_1.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.projectwork_1.domain.AppInteractor
//import com.example.projectwork_1.data.entity.Film
//import javax.inject.Inject
//
//class FavoritesFragmentViewModel @Inject constructor(private val interactor: AppInteractor) : ViewModel() {
//    val favFilmsLiveData = MutableLiveData<List<Film>>()
//    init {
//        val favFilms = interactor.getFavFilmsDB()
//        favFilmsLiveData.postValue(favFilms)
//    }
//}