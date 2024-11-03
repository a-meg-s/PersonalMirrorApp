package com.example.uimirror.database.typeconverters


import androidx.room.TypeConverter
import com.example.uimirror.database.models.Alarm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SingleAlarmTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAlarm(alarm: Alarm): String {
        return gson.toJson(alarm)
    }

    @TypeConverter
    fun toAlarm(alarmJson: String): Alarm {
        return gson.fromJson(alarmJson, object : TypeToken<Alarm>() {}.type)
    }
}