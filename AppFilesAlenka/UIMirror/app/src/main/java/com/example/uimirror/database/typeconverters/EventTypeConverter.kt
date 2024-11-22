package com.example.uimirror.database.typeconverters

import androidx.room.TypeConverter
import com.example.uimirror.database.models.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EventTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromEventList(alarmList: MutableList<Event>?): String? {
        return gson.toJson(alarmList)
    }

    @TypeConverter
    fun toEventList(alarmListString: String?): MutableList<Event>? {
        if (alarmListString.isNullOrEmpty()) return mutableListOf()
        val listType = object : TypeToken<MutableList<Event>>() {}.type
        return gson.fromJson(alarmListString, listType)
    }
}