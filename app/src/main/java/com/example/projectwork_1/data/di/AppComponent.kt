package com.example.projectwork_1.data.di

import android.app.Application
import com.example.domain_api.AppProvider
import com.example.domain_api.retrofit.RetrofitProvider
import com.example.domain_retrofit.DomainProvidersFactory
import com.example.domain_room.DomainProvidersFactoryRoom
import com.example.domain_room_api.AppProviderRoom
import com.example.domain_room_api.db.DatabaseProvider
import com.example.projectwork_1.App
import com.example.projectwork_1.data.di.modules.DatabaseModule
import com.example.projectwork_1.data.di.modules.DomainModule
import com.example.projectwork_1.di_retrofit.AppComponentRetrofit
import com.example.projectwork_1.di_room.AppComponentRoom
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DatabaseModule::class, DomainModule::class, DatabaseModule.BindModule::class],
    dependencies = [
        AppProvider::class,
        RetrofitProvider::class,
        DatabaseProvider::class]
    )
interface AppComponent {

    companion object {
        fun init(application: Application): AppComponent =
            DaggerAppComponent.builder().appProvider(AppComponentRetrofit.create(application))
                .retrofitProvider(DomainProvidersFactory.createRetrofitBuilder(AppComponentRetrofit.create(application)))
                .databaseProvider(DomainProvidersFactoryRoom.createDatabaseBuilder(AppComponentRoom.create(application)))
                .build()
    }

    fun inject(sharedFilmsViewModel: SharedFilmsViewModel)
    fun inject(appClass: App)
}