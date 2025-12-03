package com.example.domain_retrofit_impl.di

import com.example.domain_api.AppProvider
import com.example.domain_api.retrofit.RetrofitProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [RetrofitModule::class]
)
interface RetrofitComponent : RetrofitProvider