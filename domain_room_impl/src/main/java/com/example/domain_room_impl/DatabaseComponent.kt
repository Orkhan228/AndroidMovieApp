package com.example.domain_room_impl

import com.example.domain_room_api.AppProviderRoom
import com.example.domain_room_api.db.DatabaseProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProviderRoom::class],
    modules = [DatabaseModule::class]
)
interface DatabaseComponent : DatabaseProvider