package com.example.domain_retrofit

import com.example.domain_api.AppProvider
import com.example.domain_api.retrofit.RetrofitProvider
import com.example.domain_retrofit_impl.di.DaggerRetrofitComponent

object DomainProvidersFactory {
    fun createRetrofitBuilder(appProvider: AppProvider): RetrofitProvider =
        DaggerRetrofitComponent.builder().appProvider(appProvider).build()
}