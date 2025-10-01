package com.example.projectwork_1.data

import com.example.projectwork_1.domain.Film

interface AppRepository {
    val favoriteFilms: MutableList<Film>
}