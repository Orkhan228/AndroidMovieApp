package com.example.projectwork_1.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class MainRetrofit @Inject constructor(val appOkHttpClient: AppOkHttpClient) : AppRetrofit {
    override val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(appOkHttpClient.okHttpClient)
        .build()

}