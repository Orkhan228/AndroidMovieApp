package com.example.domain_room

import com.example.domain_room_api.AppProviderRoom
import com.example.domain_room_api.db.DatabaseProvider
import com.example.domain_room_impl.DaggerDatabaseComponent

object DomainProvidersFactoryRoom {
    fun createDatabaseBuilder(appProviderRoom: AppProviderRoom): DatabaseProvider {
        return DaggerDatabaseComponent.builder().appProviderRoom(appProviderRoom).build()
    }
}