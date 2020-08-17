/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.data.db.category

import androidx.room.TypeConverter

/**
 * Serialization and deserialization of [WidgetType] for storage in the application database
 *
 * _Example_: [WidgetType.PLAIN] is converted to `PLAIN` and vice versa
 *
 * _Note_: The functions are type-safe since we expect input files to be correct.
 *
 * @see WidgetType
 */
internal class WidgetTypeConverters {
    /** Convert [WidgetType] to [String] */
    @TypeConverter
    fun widgetToString(value: WidgetType) = value.name

    /** Convert [String] to [WidgetType] */
    @TypeConverter
    fun stringToWidget(value: String): WidgetType = WidgetType.valueOf(value)
}
