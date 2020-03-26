package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.WidgetType

abstract class Widget(
    private val parent: ViewGroup,
    private val listener: WidgetListener,
    private val category: Category,
    private val properties: List<PropertyWithRelations>
) {
    abstract fun createView(): View
    abstract fun createContextMenu(): AlertDialog?

    fun create(): View {
        return createView().apply {
            this.setOnLongClickListener {
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
        properties: List<PropertyWithRelations>
    ): Widget {
        return when (category.widget) {
            WidgetType.AUDIO -> AudioWidget(parent, listener, category, properties)
            WidgetType.EXAMPLE -> BaseWidget(parent, listener, category, properties)
            WidgetType.KEY_VALUE -> BaseWidget(parent, listener, category, properties)
            WidgetType.MORPHOLOGY -> MorphologyWidget(parent, listener, category, properties)
            WidgetType.PLAIN -> BaseWidget(parent, listener, category, properties)
            WidgetType.REFERENCE -> BaseWidget(parent, listener, category, properties)
            WidgetType.URL -> UrlWidget(parent, listener, category, properties)
        }
    }
}
