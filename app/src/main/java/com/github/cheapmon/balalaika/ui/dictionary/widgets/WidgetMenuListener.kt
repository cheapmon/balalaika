package com.github.cheapmon.balalaika.ui.dictionary.widgets

import com.github.cheapmon.balalaika.model.DataCategory

interface WidgetMenuListener {
    fun onClickMenuItem(item: String, category: DataCategory)
}
