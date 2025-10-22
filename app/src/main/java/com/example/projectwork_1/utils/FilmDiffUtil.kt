package com.example.projectwork_1.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.projectwork_1.data.entity.Film

class FilmDiffUtil(val oldList: List<Film>, val newList: List<Film>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].poster == newList[newItemPosition].poster &&
                oldList[oldItemPosition].description == newList[newItemPosition].description
    }
}