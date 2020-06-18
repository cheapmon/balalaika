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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithCategory
import com.github.cheapmon.balalaika.databinding.HelperItalicBinding
import com.github.cheapmon.balalaika.databinding.WidgetPlainBinding
import com.github.cheapmon.balalaika.util.highlight

/**
 * Simple widget for only displaying a single property value
 *
 * Values are displayed as-is, without additional modification.
 *
 * @see WidgetType.PLAIN
 */
open class PlainWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithCategory>,
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
        property: PropertyWithCategory
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
