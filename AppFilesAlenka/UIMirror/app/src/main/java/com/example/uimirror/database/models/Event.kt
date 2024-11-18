package com.example.uimirror.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: Long,
    val eventName: String
)