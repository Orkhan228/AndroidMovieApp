package com.example.projectwork_1.data.di.modules

import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.MainRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Module
    interface BindModule {
        @Binds
        @Singleton
        fun bindMainRepository(mainRepository: MainRepository): AppRepository
    }
}