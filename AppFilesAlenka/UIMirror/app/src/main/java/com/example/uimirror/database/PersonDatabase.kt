package com.example.uimirror.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uimirror.database.models.Alarm
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.typeconverters.LongTypeConverter
import com.example.uimirror.database.models.Music
import com.example.uimirror.database.typeconverters.MusicTypeConverter
import com.example.uimirror.database.models.Person
import com.example.uimirror.database.typeconverters.ByteTypeConverter
import com.example.uimirror.database.typeconverters.EventTypeConverter
import com.example.uimirror.database.typeconverters.SingleAlarmTypeConverter

@Database(entities = [Person::class, Music::class, Alarm::class, Event::class], version = 5, exportSchema = false,)
@TypeConverters(SingleAlarmTypeConverter::class, MusicTypeConverter::class, LongTypeConverter::class, ByteTypeConverter::class, EventTypeConverter::class)
abstract class PersonDatabase: RoomDatabase() {
    abstract fun uiMirrorDao(): PersonDao
}