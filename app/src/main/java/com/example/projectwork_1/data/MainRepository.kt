package com.example.projectwork_1.data

import com.example.projectwork_1.domain.Film


class MainRepository : AppRepository{
    override val favoriteFilms = mutableListOf<Film>()
}