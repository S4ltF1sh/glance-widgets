package com.s4ltf1sh.glance_widgets.db

import androidx.room.TypeConverter
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WidgetConverters {

    @TypeConverter
    fun fromWidgetType(type: GlanceWidgetType): String = type.typeId

    @TypeConverter
    fun toWidgetType(typeId: String): GlanceWidgetType {
        return GlanceWidgetType.fromTypeId(typeId) ?: GlanceWidgetType.fromLegacyEnum(typeId)
    }

    @TypeConverter
    fun fromWidgetSize(size: GlanceWidgetSize): String = size.name

    @TypeConverter
    fun toWidgetSize(size: String): GlanceWidgetSize = GlanceWidgetSize.valueOf(size)

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
