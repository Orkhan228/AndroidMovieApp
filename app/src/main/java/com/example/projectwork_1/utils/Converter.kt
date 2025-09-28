package com.example.projectwork_1.utils

import com.example.projectwork_1.data.entity.TmdbFilm
import com.example.projectwork_1.domain.Film

object Converter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?) : List<Film> {
        val result = mutableListOf<Film>()
        list?.forEach {
            result.add(Film(
                title = it.title,
                poster = it.posterPath,
                description = it.overview,
                rating = it.voteAverage,
                isInFavorites = false
            ))
        }
        return result
    }
}