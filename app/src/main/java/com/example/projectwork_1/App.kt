package com.example.projectwork_1

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.projectwork_1.data.di.AppComponent
import com.example.projectwork_1.data.di.DaggerAppComponent
import com.example.projectwork_1.data.sharedPref.AppPreferenceProvider
import com.example.projectwork_1.data.sharedPref.PreferenceProvider
import javax.inject.Inject


class App : Application() {

    lateinit var dagger: AppComponent
    @Inject
    lateinit var preferenceProvider: AppPreferenceProvider

    override fun onCreate() {
        super.onCreate()
        instance = this
        dagger = DaggerAppComponent.builder().appContext(context = this).build()
        dagger.inject(this)

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

    companion object {
        lateinit var instance: App
            private set
    }
}