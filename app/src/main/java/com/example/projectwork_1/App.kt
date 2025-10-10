package com.example.projectwork_1

import android.app.Application
import com.example.projectwork_1.data.di.AppComponent
import com.example.projectwork_1.data.di.DaggerAppComponent


class App : Application() {

    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        dagger = DaggerAppComponent.create()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}