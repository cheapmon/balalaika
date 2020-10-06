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
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.databinding.HelperExampleBinding
import com.github.cheapmon.balalaika.databinding.WidgetPlainBinding
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property

/**
 * Widget for example sentences and full-text information
 *
 * @see Property.Example
 */
class ExampleWidget(
    parent: ViewGroup,
    category: DataCategory,
    properties: List<Property.Example>,
    menuListener: WidgetMenuListener
) : Widget<Property.Example>(
    parent,
    category,
    properties,
    false,
    menuListener
) {
    override fun createContentView(inflater: LayoutInflater, category: DataCategory): Views {
        val binding: WidgetPlainBinding = WidgetPlainBinding.inflate(inflater)
        return Views(binding.root, binding.widgetPlainContent)
    }

    override fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: Property.Example,
        actionListener: WidgetActionListener<Property.Example>?
    ): View = HelperExampleBinding.inflate(inflater).apply {
        title = displayName(property)
        value = property.content
    }.root

    override fun displayName(property: Property.Example): String =
        property.name

    // No meaningful context menu actions
    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
}
