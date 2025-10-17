package com.example.projectwork_1.data.di

import android.content.Context
import com.example.projectwork_1.App
import com.example.projectwork_1.data.di.modules.DatabaseModule
import com.example.projectwork_1.data.di.modules.DomainModule
import com.example.projectwork_1.data.di.modules.RemoteModule
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, DomainModule::class, RemoteModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(context: Context): Builder
        fun build(): AppComponent
    }

    fun inject(sharedFilmsViewModel: SharedFilmsViewModel)
    fun inject(appClass: App)
}