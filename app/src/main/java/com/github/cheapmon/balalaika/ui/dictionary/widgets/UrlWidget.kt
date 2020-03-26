package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.data.entities.SearchRestriction

class UrlWidget(
    private val parent: ViewGroup,
    private val listener: WidgetListener,
    private val category: Category,
    private val properties: List<PropertyWithRelations>
) : KeyValueWidget(parent, listener, category, properties) {
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

    override val menuItems: Array<String> = properties.map {
        "Search dictionary entries matching ${displayValue(it.property.value)}"
    }.toTypedArray()

    override val menuActions: List<() -> Unit> = properties.map {
        { listener.onClickSearchButton(displayValue(it.property.value), SearchRestriction.None) }
    }
}