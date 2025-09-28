package com.example.projectwork_1.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val title: String,
    val poster: String,
    val description: String,
    val rating: Double = 0.0,
    var isInFavorites: Boolean = false,
) : Parcelable