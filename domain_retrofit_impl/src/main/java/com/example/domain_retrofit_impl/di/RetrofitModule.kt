package com.example.domain_retrofit_impl.di

import com.example.domain_api.retrofit.TmdbApi
import com.example.domain_retrofit_impl.constants.ApiConstants
import com.example.domain_retrofit_impl.retrofit.MainOkHttpClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object RetrofitModule {

    @Provides
    @Singleton
    fun providesRetrofitApi(): TmdbApi =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(MainOkHttpClient().okHttpClient)
            .build()
            .create(TmdbApi::class.java)

}