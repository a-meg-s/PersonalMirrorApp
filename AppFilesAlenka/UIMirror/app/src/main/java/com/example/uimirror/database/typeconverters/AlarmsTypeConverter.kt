package com.example.uimirror.database.typeconverters

import androidx.room.TypeConverter
import com.example.uimirror.database.models.Alarm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmsTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAlarmList(alarmList: List<Alarm>?): String? {
        return gson.toJson(alarmList)
    }

    @TypeConverter
    fun toAlarmList(alarmListString: String?): List<Alarm>? {
        if (alarmListString.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Alarm>>() {}.type
        return gson.fromJson(alarmListString, listType)
    }
}