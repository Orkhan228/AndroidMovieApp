package com.example.projectwork_1.entity

import com.example.domain_room_api.entity.Film

data class WatchLaterNotification(
    val film: Film,
    val triggerTime: Long,
    val alarmRequestCode: Int,
)