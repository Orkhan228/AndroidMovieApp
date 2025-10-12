package com.example.projectwork_1.data.sharedPref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferenceProvider @Inject constructor(context: Context) : AppPreferenceProvider {
    override val appContext = context.applicationContext

    //создаем наш sharedPreference
    override val preference: SharedPreferences =
        appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    //логика, выполняемая при создании
    init {
        //Метод preference.getBoolean, берет значение по ключу, так как мы ничего в этот ключ пока не ложили,
        //используется дефолтное значение true, которое мы указали вторым параметром, а уже в самом if,
        //мы записываем методом putBoolean, значение false
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    //сохраняет новую переданную категорию в sharedPreference
    override fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }

    //Выдает категорию по дефолтному ключу, если она null, то автоматически передается "popular"
    override fun getDefaultCategory(): String =
        preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY

    override fun saveTheme(theme: String) {
        preference.edit { putString(KEY_APP_THEME, theme) }
    }

    override fun getTheme(): String = preference.getString(KEY_APP_THEME, THEME_LIGHT) ?: THEME_LIGHT

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"

        private const val KEY_APP_THEME = "app_theme"
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}