package com.example.database.store

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: List<Long>,
    val isActive: Boolean
)