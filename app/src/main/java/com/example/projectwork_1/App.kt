package com.example.projectwork_1

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.domain_api.retrofit.TmdbApi
import com.example.domain_room_api.db.FilmDao
import com.example.projectwork_1.data.di.AppComponent
import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import javax.inject.Inject


class App : Application() {

    @Inject
    lateinit var preferenceProvider: AppPreferenceProvider

    @Inject
    lateinit var tmdbApi: TmdbApi

    @Inject
    lateinit var filmDao: FilmDao

    override fun onCreate() {
        super.onCreate()
        instance = this
        getApp().inject(this)

        //этот код нам нужен для того, чтобы загружать последнюю выбранную тему, то есть перед выходом из приложения, допустим
        //что я выбрал темную схему, это сохранилось в sharedPreferences, теперь при следующим запуском приложения, приложение
        //будет знать какую тему использовать, а пишем мы это в Арр классе, потому что он создается до создания активити и фрагментов
        //получаем последнюю тему
        val theme = preferenceProvider.getTheme()
        //и вызывать логику
        if (theme == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun getApp(): AppComponent {
        return appComponent ?: AppComponent.init(this).also {
            appComponent = it
        }
    }

    companion object {
        lateinit var instance: App
            private set

        private var appComponent: AppComponent? = null
    }
}