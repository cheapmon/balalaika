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
package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithCategory

/**
 * Small user interface element for displaying [properties][Property] of [lexemes][Lexeme]
 *
 * Widgets are meant to display a group of properties that belong to the same
 * [data category][Category]. Each widget is required to define [createView] and
 * [createContextMenu]. It is up to the user interface to properly insert the resulting
 * [view][View] at the appropriate position.
 *
 * @see WidgetType
 */
abstract class Widget(
    /** Parent of the widget's view */
    private val parent: ViewGroup,
    /** Handles any widget actions requested by the user */
    private val listener: WidgetListener,
    /** Data category of [properties] */
    private val category: Category,
    /** List of properties to be displayed */
    private val properties: List<PropertyWithCategory>,
    /** Availability of action buttons to the user */
    private val hasActions: Boolean,
    /** An optional search query */
    private val searchText: String?
) {
    /** Create the widget view */
    abstract fun createView(): View

    /** Create a context menu for the widget */
    abstract fun createContextMenu(): AlertDialog?

    /** Combine [createView] and [createContextMenu] */
    fun create(): View {
        return createView().apply {
            if (hasActions) this.setOnLongClickListener {
                createContextMenu()?.show()
                true
            }
        }
    }
}

/** Component that handles [Widget] actions */
interface WidgetListener {
    /** Callback for when an audio action button is clicked */
    fun onClickAudioButton(resId: Int)

    /** Callback for when a search action button is clicked */
    fun onClickSearchButton(query: String, restriction: SearchRestriction)

    /** Callback for when a scroll action button is clicked */
    fun onClickScrollButton(externalId: String)

    /** Callback for when a link action button is clicked */
    fun onClickLinkButton(link: String)
}

/** Helper object to assign appropriate [widgets][Widget] to a list of [properties][Property] */
object Widgets {
    /** Assign appropriate widget */
    fun get(
        parent: ViewGroup,
        listener: WidgetListener,
        category: Category,
        properties: List<PropertyWithCategory>,
        hasActions: Boolean = true,
        searchText: String? = null
    ): Widget {
        return when (category.widget) {
            WidgetType.AUDIO -> AudioWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.EXAMPLE -> ExampleWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.KEY_VALUE -> BaseWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.MORPHOLOGY -> MorphologyWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.PLAIN -> PlainWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.REFERENCE -> ReferenceWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
            WidgetType.URL -> UrlWidget(
                parent,
                listener,
                category,
                properties,
                hasActions,
                searchText
            )
        }
    }
}
