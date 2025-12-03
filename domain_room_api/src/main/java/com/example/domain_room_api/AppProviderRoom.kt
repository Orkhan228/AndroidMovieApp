package com.example.domain_room_api

import android.content.Context
import com.example.domain_api.AppProvider

interface AppProviderRoom : AppProvider {
    override fun provideContext(): Context
}