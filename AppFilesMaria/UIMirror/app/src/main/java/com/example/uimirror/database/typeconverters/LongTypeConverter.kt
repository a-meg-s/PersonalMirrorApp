package com.example.uimirror.database.typeconverters

import androidx.room.TypeConverter

class LongTypeConverter {

    /*
    eg: [111111111, 22222222, 3333333 , 44444444]

    val stringTimeList = "111111111,22222222,3333333,44444444"

     */
    @TypeConverter
    fun fromDateTimeList(dateTimeList: List<Long>?): String? {
        return dateTimeList?.joinToString(separator = ",")
    }


    /**
     * val stringTimeList = "111111111,22222222,3333333,44444444"
     *
     *
     * 111111111.toLong()
     * 22222222.toLong()
     * 3333333.toLong()
     * 44444444.toLong()
     * [111111111, 22222222, 3333333 , 44444444]
     *
     *
     * val stringTimeList = null
     *
     * []
     *
     */

    @TypeConverter
    fun toDateTimeList(dateTimeString: String?): List<Long>? {
        return dateTimeString?.split(",")?.map { it.toLong() } ?: emptyList()
    }
}