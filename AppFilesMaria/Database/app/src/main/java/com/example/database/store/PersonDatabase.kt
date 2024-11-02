package com.example.database.store

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Person::class, Music::class, Alarm::class], version = 1, exportSchema = false)
@TypeConverters(AlarmTypeConverter::class, MusicTypeConverter::class)
abstract class PersonDatabase: RoomDatabase() {
    abstract fun personDao(): PersonDao
}