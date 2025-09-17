package com.example.projectwork_1.domain

import com.example.projectwork_1.data.MainRepository

class Interactor(val mainRepo: MainRepository ) {
    fun getFilmsDB() : List<Film> {
        return mainRepo.dataBase
    }
    fun getFavFilmsDB() : List<Film> {
        return mainRepo.favoriteFilms
    }
    fun addFavFilmsToDB(film: Film) {
        mainRepo.favoriteFilms.add(film)
    }
    fun removeFavFilmsFromDB(film: Film) {
        mainRepo.favoriteFilms.remove(film)
    }
}