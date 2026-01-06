package com.rui.base.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 列表类型转换器
 */
class ListConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        if (value == null) return null
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}
