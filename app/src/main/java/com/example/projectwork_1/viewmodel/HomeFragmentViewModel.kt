package com.example.projectwork_1.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectwork_1.App
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.domain.Interactor

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private lateinit var interactor: Interactor
    init {
        interactor = App.instance.interactor
        val films = interactor.getFilmsDB()
        filmsListLiveData.postValue(films)
    }
}