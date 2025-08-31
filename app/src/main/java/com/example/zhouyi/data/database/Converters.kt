package com.example.zhouyi.data.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.*

/**
 * Room数据库类型转换器
 * 用于JSON字符串与对象列表之间的转换
 */
class Converters {
    private val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    private val intListType = Types.newParameterizedType(List::class.java, Int::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

    private val intListAdapter = moshi.adapter<List<Int>>(intListType)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    /**
     * 将Int列表转换为JSON字符串
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { intListAdapter.toJson(it) }
    }

    /**
     * 将JSON字符串转换为Int列表
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let { intListAdapter.fromJson(it) }
    }

    /**
     * 将String列表转换为JSON字符串
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { stringListAdapter.toJson(it) }
    }

    /**
     * 将JSON字符串转换为String列表
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { stringListAdapter.fromJson(it) }
    }

    /**
     * 将Long时间戳转换为Date对象
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * 将Date对象转换为Long时间戳
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
