package com.example.projectwork_1.data.di

import com.example.projectwork_1.data.AppRepository
import com.example.projectwork_1.data.MainRepository
import com.example.projectwork_1.domain.AppInteractor
import com.example.projectwork_1.domain.Interactor
import com.example.projectwork_1.utils.Remote
import com.example.projectwork_1.utils.TmdbApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    fun providesRepo() : AppRepository = MainRepository()

    @Provides
    fun providesRemote() : Remote = Remote()

    @Provides
    fun providesRetrofitService(remote: Remote) : TmdbApi = remote.retrofitService

    @Provides
    fun providesInteractor(repo: AppRepository, retrofitService: TmdbApi) : AppInteractor =
        Interactor(repo, retrofitService)
}