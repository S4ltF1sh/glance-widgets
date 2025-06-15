package com.s4ltf1sh.glance_widgets.db

import androidx.room.TypeConverter
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WidgetConverters {
    @TypeConverter
    fun fromWidgetType(type: WidgetType): String = type.name

    @TypeConverter
    fun toWidgetType(type: String): WidgetType = WidgetType.valueOf(type)

    @TypeConverter
    fun fromWidgetSize(size: WidgetSize): String = size.name

    @TypeConverter
    fun toWidgetSize(size: String): WidgetSize = WidgetSize.valueOf(size)

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
            .toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
            .fromJson(value) ?: emptyList()
    }
}
