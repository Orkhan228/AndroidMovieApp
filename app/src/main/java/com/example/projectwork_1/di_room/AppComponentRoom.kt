package com.example.projectwork_1.di_room

import android.app.Application
import android.content.Context
import com.example.domain_api.AppProvider
import com.example.domain_room_api.AppProviderRoom
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponentRoom : AppProviderRoom {

    companion object {
        private var appComponentRoom: AppProviderRoom? = null
        fun create(application: Application): AppProviderRoom {
            return appComponentRoom ?: DaggerAppComponentRoom.builder()
                .application(application.applicationContext)
                .build().also {
                    appComponentRoom = it
                }
        }
    }

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder
        fun build(): AppComponentRoom
    }

}