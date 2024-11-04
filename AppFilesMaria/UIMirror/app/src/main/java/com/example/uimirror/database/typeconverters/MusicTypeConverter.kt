package com.example.uimirror.database.typeconverters

import androidx.room.TypeConverter
import com.example.uimirror.database.models.Music
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MusicTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMusicList(musicList: List<Music>?): String? {
        return gson.toJson(musicList)
    }

    @TypeConverter
    fun toMusicList(musicListString: String?): List<Music>? {
        if (musicListString.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<Music>>() {}.type
        return gson.fromJson(musicListString, listType)
    }

}