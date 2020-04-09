package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations

class UrlWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithRelations>,
    hasActions: Boolean,
    searchText: String?
) : BaseWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun displayValue(value: String): String {
        return value.split(Regex(";;;")).firstOrNull() ?: ""
    }

    override fun actionIcon(value: String): Int? {
        value.split(Regex(";;;")).getOrNull(1) ?: return null
        return R.drawable.ic_link
    }

    override fun onClickActionButtonListener(value: String) {
        val link = value.split(Regex(";;;")).getOrNull(1) ?: return
        listener.onClickLinkButton(link)
    }
}