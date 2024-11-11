package com.example.uimirror.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uimirror.database.models.Alarm

@Entity
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var faceData: ByteArray,
    var alarm: Alarm? = null,
    var selectedSongId: Int? = null, // ID des ausgewählten Songs für die Person
    var songPosition: Int = 0, // Fortschritt oder Position im Song
    var isMusicEnabled: Boolean = true,
    var isPrimaryUser: Boolean = false
)