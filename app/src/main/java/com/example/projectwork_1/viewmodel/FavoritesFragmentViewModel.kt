package com.example.projectwork_1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectwork_1.domain.AppInteractor
import com.example.projectwork_1.domain.Film
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesFragmentViewModel @Inject constructor(private val interactor: AppInteractor) : ViewModel() {
    val favFilmsLiveData = MutableLiveData<List<Film>>()
    init {
        val favFilms = interactor.getFavFilmsDB()
        favFilmsLiveData.postValue(favFilms)
    }
}