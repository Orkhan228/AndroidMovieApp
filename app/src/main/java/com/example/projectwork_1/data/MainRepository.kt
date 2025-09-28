package com.example.projectwork_1.data

import com.example.projectwork_1.utils.API
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.R
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainRepository{
    val favoriteFilms = mutableListOf<Film>()
}