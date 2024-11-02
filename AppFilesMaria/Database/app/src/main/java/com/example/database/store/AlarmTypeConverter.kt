package com.example.database.store

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class AlarmTypeConverter {
    @TypeConverter
    fun fromAlarmModel(value: Alarm): String = Gson().toJson(value)

    @TypeConverter
    fun toAlarmModel(value: String): Alarm = Gson().fromJson(value, Alarm::class.java)

    @TypeConverter
    fun stringToListAlarms(data: String?): List<Alarm?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Alarm?>?>() {}.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun listAlarmsToString(someObjects: List<Alarm?>?): String? {
        return Gson().toJson(someObjects)
    }
}