package com.example.uimirror.database.typeconverters

import androidx.room.TypeConverter
import com.example.uimirror.database.models.Alarm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ByteTypeConverter {
    @TypeConverter
    fun fromListByte(faceData: List<Byte>): ByteArray {
        return faceData.toByteArray()
    }

    @TypeConverter
    fun toListByte(data: ByteArray): List<Byte> {
        return data.toList()
    }
}