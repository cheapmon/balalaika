package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property

/**
 * Simple widget displaying a value and its data category
 *
 * @see Property.Simple
 */
class SimpleWidget(
    parent: ViewGroup,
    category: DataCategory,
    properties: List<Property.Simple>,
    menuListener: WidgetMenuListener
) : Widget<Property.Simple>(parent, category, properties, false, menuListener) {
    override fun displayName(property: Property.Simple): String =
        property.value
}
