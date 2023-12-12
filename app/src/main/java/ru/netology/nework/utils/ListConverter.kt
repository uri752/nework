package ru.netology.nework.utils

import androidx.room.TypeConverter

class ListConverter {

    private companion object {
        const val SEPARATOR = ","
    }

    @TypeConverter
    fun fromListLongToString(longList: List<Long>): String = longList.joinToString(SEPARATOR)

    @TypeConverter
    fun toListLongFromString(stringList: String): List<Long> = stringList.split(SEPARATOR)
        .mapNotNull { it.toLongOrNull() }
}