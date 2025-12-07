package com.example.projectwork_1.di_retrofit

import android.app.Application
import android.content.Context
import com.example.domain_api.AppProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponentRetrofit : AppProvider {

    companion object {
        private var appComponentRetrofit: AppProvider? = null
        fun create(application: Application): AppProvider {
            return appComponentRetrofit ?: DaggerAppComponentRetrofit.builder()
                .application(application.applicationContext)
                .build().also {
                    appComponentRetrofit = it
                }
        }
    }

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder
        fun build(): AppComponentRetrofit
    }

}