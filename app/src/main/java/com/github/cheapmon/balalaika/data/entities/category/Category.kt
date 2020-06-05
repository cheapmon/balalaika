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
package com.github.cheapmon.balalaika.data.entities.category

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.util.ResourceUtil

/**
 * Data category of a dictionary entry
 *
 * A dictionary might hold many different pieces of information, for example:
 * - Orthographic information
 * - Part of speech
 * - Morphology
 * - Example sentences
 * - Pronunciation of words
 *
 * Additionally, each piece of information might be displayed differently.
 * Since a lot of languages are only sparsely documented and might require very unique data
 * categories to represent information, we cannot possibly provide all data categories which might
 * be used in a dictionary context.
 *
 * Instead of data category _type_, we simply differentiate based on data category _presentation_.
 * This is done through the [widget] property. For example, pieces of information which do not
 * represent the same type of data, but share a common presentation will have the same [WidgetType],
 * but not the same [Category]. This kind of differentiation enables a flexible and wide-spread
 * language support, which is one of the main goals of this project.
 *
 * For additonal information on Balalaika's data model, please refer to the
 * [Import documentation][com.github.cheapmon.balalaika.data.insert].
 *
 * @see WidgetType
 */
@Entity(
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["sequence"], unique = true)
    ]
)
data class Category(
    /**
     * Primary key of this category
     *
     * _Note_: A default value of `0` and `autoGenerate = true` effectively corresponds to
     * auto increment.
     */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val categoryId: Long = 0,
    /** Unique primary identifier of this category from sources */
    @ColumnInfo(name = "external_id") val externalId: String,
    /** Display name of this category for the user interface */
    @ColumnInfo(name = "name") val name: String,
    /**
     * Data category presentation in the user interface
     *
     * @see WidgetType
     */
    @ColumnInfo(name = "widget") val widget: WidgetType,
    /**
     * Icon of this data category shown in the user interface
     *
     * References the identifier of a [Drawable].
     *
     * @see ResourceUtil
     */
    @ColumnInfo(name = "icon_id") val iconId: String,
    /**
     * Unique position in ordering of data categories
     *
     * Data categories with a smaller `sequence` are shown higher in the dictionary entry.
     */
    @ColumnInfo(name = "sequence") val sequence: Int,
    /**
     * Visibility of data category in the user interface
     *
     * Some data categories may be used for metadata purposes, but aren't meant to be shown to
     * the user. A hidden data category will be invisible to the user, but can still be used for
     * searching and ordering of dictionary entries.
     */
    @ColumnInfo(name = "hidden") val hidden: Boolean,
    /**
     * Sortability of data category
     *
     * Dictionary entries may be ordered not only by their lexeme or full form, but also by some
     * of their properties, which needs to be configurable since
     * it isn't helpful for dictionary entries to be ordered by any category (e.g. URLs).
     */
    @ColumnInfo(name = "order_by") val orderBy: Boolean
)
