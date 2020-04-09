package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.category.WidgetType

abstract class Widget(
    private val parent: ViewGroup,
    private val listener: WidgetListener,
    private val category: Category,
    private val properties: List<PropertyWithRelations>,
    private val hasActions: Boolean,
    private val searchText: String?
) {
    abstract fun createView(): View
    abstract fun createContextMenu(): AlertDialog?

    fun create(): View {
        return createView().apply {
            if (hasActions) this.setOnLongClickListener {
                createContextMenu()?.show()
                true
            }
        }
    }
}

interface WidgetListener {
    fun onClickAudioButton(resId: Int)
    fun onClickSearchButton(query: String, restriction: SearchRestriction)
    fun onClickScrollButton(externalId: String)
    fun onClickLinkButton(link: String)
}

object Widgets {
    fun get(
        parent: ViewGroup,
        listener: WidgetListener,
        category: Category,
        properties: List<PropertyWithRelations>,
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
