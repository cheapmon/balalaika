package com.github.cheapmon.balalaika.data.entities.category

import androidx.room.TypeConverter

class WidgetTypeConverters {
    @TypeConverter
    fun widgetToString(value: WidgetType) = value.name

    @TypeConverter
    fun stringToWidget(value: String): WidgetType = WidgetType.valueOf(value)
}