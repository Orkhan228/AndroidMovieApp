package com.example.projectwork_1.utils

import javax.inject.Inject

class MainTmdbApi @Inject constructor(retrofit: AppRetrofit) : AppTmdbApi {
    override val api: TmdbApi = retrofit.retrofit.create(TmdbApi::class.java)
}