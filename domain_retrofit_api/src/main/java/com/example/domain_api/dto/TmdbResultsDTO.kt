package com.example.domain_api.dto

import com.google.gson.annotations.SerializedName

data class TmdbResultsDTO(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val tmdbFilms: List<TmdbFilm>,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("total_results")
    val total_results: Int
)