package com.example.projectwork_1.data

import com.example.projectwork_1.domain.Film
import javax.inject.Inject

class MainRepository @Inject constructor() : AppRepository{
    override val favoriteFilms = mutableListOf<Film>()
}