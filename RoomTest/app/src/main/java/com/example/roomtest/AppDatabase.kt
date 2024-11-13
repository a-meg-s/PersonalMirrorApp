package com.example.roomtest

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserFaceEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userFaceDao(): UserFaceDao
}