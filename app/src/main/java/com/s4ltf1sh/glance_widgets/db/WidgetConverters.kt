package com.s4ltf1sh.glance_widgets.db

import androidx.room.TypeConverter
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType

class WidgetConverters {
    @TypeConverter
    fun fromWidgetType(type: WidgetType): String = type.name

    @TypeConverter
    fun toWidgetType(type: String): WidgetType = WidgetType.valueOf(type)

    @TypeConverter
    fun fromWidgetSize(size: WidgetSize): String = size.name

    @TypeConverter
    fun toWidgetSize(size: String): WidgetSize = WidgetSize.valueOf(size)
}
