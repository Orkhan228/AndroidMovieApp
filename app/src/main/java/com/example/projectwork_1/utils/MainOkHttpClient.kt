package com.example.projectwork_1.utils

import com.example.projectwork_1.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class MainOkHttpClient@Inject constructor() : AppOkHttpClient {
    override val okHttpClient = OkHttpClient.Builder()
        .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG)
                level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
}