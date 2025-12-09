package com.example.projectwork_1

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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

    lateinit var receiver: MyChargerBroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        instance = this
        getApp().inject(this)

        //этот код нам нужен для того, чтобы загружать последнюю выбранную тему, то есть перед выходом из приложения, допустим
        //что я выбрал темную тему, это сохранилось в sharedPreferences, теперь при следующим запуском приложения, приложение
        //будет знать какую тему использовать, а пишем мы это в Арр классе, потому что он создается до создания активити и фрагментов
        //получаем последнюю тему
        val theme = preferenceProvider.getTheme()
        //и вызывать логику
        if (theme == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        receiver = MyChargerBroadcastReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }

        //так как класс App живет на протяжении всего приложения, то дерегистрировать receiver не надо
        this.registerReceiver(
            receiver,
            intentFilter
        )
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

    //Решил BroadcastReceiver сделать с контекстом всего приложения
    inner class MyChargerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                Intent.ACTION_POWER_CONNECTED ->  {
                    if (preferenceProvider.getTheme() == "dark") {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        preferenceProvider.saveTheme("light")
                    }
                }

                Intent.ACTION_BATTERY_LOW -> {
                    if (preferenceProvider.getTheme() == "light") {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        preferenceProvider.saveTheme("dark")
                    }
                }
            }
        }
    }
}