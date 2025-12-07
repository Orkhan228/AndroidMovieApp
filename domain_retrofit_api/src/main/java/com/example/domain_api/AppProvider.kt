package com.example.domain_api

import android.content.Context

interface AppProvider {
    fun provideContext(): Context
}