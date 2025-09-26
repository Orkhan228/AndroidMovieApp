package com.example.projectwork_1

import android.app.Application
import android.icu.util.TimeUnit
import com.example.projectwork_1.data.MainRepository
import com.example.projectwork_1.domain.Interactor
import com.example.projectwork_1.utils.ApiConstants
import com.example.projectwork_1.utils.TmdbApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    lateinit var repository: MainRepository
    lateinit var interactor: Interactor
    lateinit var okHttpClient: OkHttpClient
    lateinit var retrofit: Retrofit
    lateinit var interceptor: HttpLoggingInterceptor
    lateinit var retrofitService: TmdbApi

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = MainRepository()

        interceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG)
            level = HttpLoggingInterceptor.Level.BASIC
        }

        okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofitService = retrofit.create(TmdbApi::class.java)

        interactor = Interactor(repository, retrofitService)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}