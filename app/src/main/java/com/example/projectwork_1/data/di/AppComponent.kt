package com.example.projectwork_1.data.di

import com.example.projectwork_1.data.di.modules.DatabaseModule
import com.example.projectwork_1.data.di.modules.DomainModule
import com.example.projectwork_1.data.di.modules.RemoteModule
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, DomainModule::class, RemoteModule::class])
interface AppComponent {
    fun inject(sharedFilmsViewModel: SharedFilmsViewModel)
}