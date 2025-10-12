package com.example.projectwork_1.data.sharedPref

import android.content.Context
import android.content.SharedPreferences


interface AppPreferenceProvider {
    val appContext: Context
    val preference: SharedPreferences
    fun saveDefaultCategory(category: String)
    fun getDefaultCategory(): String
    fun saveTheme(theme: String)
    fun getTheme(): String
}