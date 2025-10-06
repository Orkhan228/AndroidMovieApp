package com.example.projectwork_1.data.di.modules

import com.example.projectwork_1.BuildConfig
import com.example.projectwork_1.utils.ApiConstants
import com.example.projectwork_1.utils.AppOkHttpClient
import com.example.projectwork_1.utils.AppRetrofit
import com.example.projectwork_1.utils.AppTmdbApi
import com.example.projectwork_1.utils.MainOkHttpClient
import com.example.projectwork_1.utils.MainRetrofit
import com.example.projectwork_1.utils.MainTmdbApi
import dagger.Binds
import dagger.Module

import javax.inject.Singleton

@Module
abstract class RemoteModule {

    @Binds
    @Singleton
    abstract fun bindOkHttpClient(okHttpClient: MainOkHttpClient): AppOkHttpClient

    @Binds
    @Singleton
    abstract fun bindRetrofit(retrofit: MainRetrofit): AppRetrofit

    @Binds
    @Singleton
    abstract fun bindTmdbApiService(apiService: MainTmdbApi): AppTmdbApi

}