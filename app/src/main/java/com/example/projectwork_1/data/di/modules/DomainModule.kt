package com.example.projectwork_1.data.di.modules

import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import com.example.projectwork_1.data.sharedPref.PreferenceProvider
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DomainModule {
    @Binds
    @Singleton
    abstract fun bindsPreferenceProvider(preferenceProvider: PreferenceProvider): AppPreferenceProvider
}