package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.databinding.HelperItalicBinding
import com.github.cheapmon.balalaika.databinding.WidgetPlainBinding
import com.github.cheapmon.balalaika.util.highlight

open class PlainWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithRelations>,
    hasActions: Boolean,
    searchText: String?
) : BaseWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun createContainer(inflater: LayoutInflater): Pair<View, ViewGroup> {
        val binding: WidgetPlainBinding =
            DataBindingUtil.inflate(inflater, R.layout.widget_plain, parent, false)
        return Pair(binding.root, binding.widgetPlainContent)
    }

    override fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: PropertyWithRelations
    ): View {
        val propertyBinding: HelperItalicBinding =
            DataBindingUtil.inflate(inflater, R.layout.helper_italic, contentView, false)
        with(propertyBinding) {
            helperTextItalic.text = displayValue(property.property.value)
                .highlight(searchText, parent.context)
        }
        return propertyBinding.root
    }
}