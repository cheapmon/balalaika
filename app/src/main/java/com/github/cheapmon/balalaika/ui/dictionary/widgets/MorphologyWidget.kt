package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations

class MorphologyWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithRelations>
) : BaseWidget(parent, listener, category, properties) {
    override val menuItems: Array<String> = properties.flatMap {
        displayValue(it.property.value).split(Regex("\\|"))
    }.toTypedArray()
}