package com.example

import android.app.Application
import androidx.room.Room
import com.example.database.store.PersonDatabase

class MyApplication: Application() {
    companion object {
        lateinit var database: PersonDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ).build()
    }
}