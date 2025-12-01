package com.example.projectwork_1.utils

import com.example.projectwork_1.data.entity.TmdbResultsDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ) : Single<TmdbResultsDTO>


    @GET("search/movie")
    fun searchFilm(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("include_adult") includeAdult: Boolean = false
    ) : Single<TmdbResultsDTO>
}