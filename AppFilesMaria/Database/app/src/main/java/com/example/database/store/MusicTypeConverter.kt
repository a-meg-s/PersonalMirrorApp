package com.example.database.store

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class MusicTypeConverter {
    @TypeConverter
    fun fromMusicModel(value: Music): String = Gson().toJson(value)

    @TypeConverter
    fun toMusicModel(value: String): Music = Gson().fromJson(value, Music::class.java)

    @TypeConverter
    fun stringToListMusic(data: String?): List<Music?> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Music?>?>() {}.type
        return Gson().fromJson<List<Music?>>(data, listType)
    }

    @TypeConverter
    fun listMusicToString(someObjects: List<Music?>?): String? {
        return Gson().toJson(someObjects)
    }
}