package com.github.cheapmon.balalaika.ui.dictionary.widgets

import com.github.cheapmon.balalaika.model.Property

interface WidgetActionListener<T : Property> {
    fun onAction(property: T)
}
