package com.example.domain_room_api.db

interface DatabaseProvider {
    fun provideFilmDAO(): FilmDao
}