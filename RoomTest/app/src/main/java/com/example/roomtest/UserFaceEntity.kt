package com.example.roomtest

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_faces")
data class UserFaceEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val faceData: ByteArray
)