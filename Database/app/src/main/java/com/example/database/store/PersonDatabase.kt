package com.example.database.store

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 1)
abstract class PersonDatabase: RoomDatabase() {
    abstract fun personDao(): PersonDao
}