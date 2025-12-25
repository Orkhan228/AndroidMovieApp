package com.example.projectwork_1.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.projectwork_1.entity.WatchLaterNotification

class WatchLaterFilmDiffUtil (val oldList: List<WatchLaterNotification>, val newList: List<WatchLaterNotification>) :
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
        return oldList[oldItemPosition].film.title == newList[newItemPosition].film.title
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].film.poster == newList[newItemPosition].film.poster &&
                oldList[oldItemPosition].film.description == newList[newItemPosition].film.description &&
                oldList[oldItemPosition].triggerTime == newList[newItemPosition].triggerTime
    }
}
