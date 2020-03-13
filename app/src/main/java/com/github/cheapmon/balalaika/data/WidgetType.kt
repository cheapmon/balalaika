package com.github.cheapmon.balalaika.data

import androidx.room.TypeConverter

enum class WidgetType {
    AUDIO,
    EXAMPLE,
    KEY_VALUE,
    PLAIN,
    REFERENCE,
    TITLE,
    URL
}

class Converters {
    @TypeConverter
    fun widgetToString(value: WidgetType) = value.name

    @TypeConverter
    fun stringToWidget(value: String): WidgetType = WidgetType.valueOf(value)
}
