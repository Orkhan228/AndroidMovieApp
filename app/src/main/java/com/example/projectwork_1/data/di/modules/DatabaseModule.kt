package com.example.projectwork_1.data.di.modules

import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.MainRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindMainRepository(mainRepository: MainRepository): AppRepository
}