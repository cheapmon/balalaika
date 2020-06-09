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
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations
import com.github.cheapmon.balalaika.databinding.HelperButtonBinding
import com.github.cheapmon.balalaika.databinding.WidgetTemplateBinding
import com.github.cheapmon.balalaika.util.ResourceUtil
import com.github.cheapmon.balalaika.util.highlight
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Simple key-value widget
 *
 * This class provides a very simple and flexible architecture for all other widgets to inherit
 * from. Most of the time it suffices to switch out some of the provided functions to achieve the
 * desired widget functionality.
 */
open class BaseWidget(
    /** @suppress */
    val parent: ViewGroup,
    /** @suppress */
    val listener: WidgetListener,
    /** @suppress */
    val category: Category,
    /** @suppress */
    val properties: List<PropertyWithRelations>,
    /** @suppress */
    private val hasActions: Boolean,
    /** @suppress */
    val searchText: String?
) : Widget(parent, listener, category, properties, hasActions, searchText) {
    /** Create container and insert one view per property */
    override fun createView(): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val (root, contentView) = createContainer(layoutInflater)
        properties.forEach { property ->
            val propertyView = createPropertyView(layoutInflater, contentView, property)
            contentView.addView(propertyView)
        }
        return root
    }

    /** Create a simple context menu, based on [menuMessage] and [menuActions] */
    override fun createContextMenu(): AlertDialog? = MaterialAlertDialogBuilder(parent.context)
        .setIcon(categoryIcon)
        .setTitle(menuMessage)
        .setNegativeButton(R.string.dictionary_item_cancel, null)
        .setItems(menuItems) { _, which -> menuActions[which]() }
        .show()

    /** Create container for property views */
    open fun createContainer(inflater: LayoutInflater): Pair<View, ViewGroup> {
        val binding: WidgetTemplateBinding =
            DataBindingUtil.inflate(inflater, R.layout.widget_template, parent, false)
        // Display lexeme form and data category icon
        with(binding) {
            title = category.name
            widgetTemplateIcon.setImageResource(categoryIcon)
        }
        return Pair(binding.root, binding.widgetTemplateContent)
    }

    /** Create view for a single property */
    open fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: PropertyWithRelations
    ): View {
        val propertyBinding: HelperButtonBinding =
            DataBindingUtil.inflate(inflater, R.layout.helper_button, contentView, false)
        with(propertyBinding) {
            // Display property value
            helperText.text = displayValue(property.property.value)
                .highlight(searchText, contentView.context)
            // Display action button and add listener
            val icon = if (hasActions) actionIcon(property.property.value) else null
            if (icon != null) helperButton.setImageResource(icon)
            else helperButton.visibility = View.GONE
            helperButton.setOnClickListener { onClickActionButtonListener(property.property.value) }
        }
        return propertyBinding.root
    }

    /** Transform a single property value for display in the user interface */
    open fun displayValue(value: String) = value

    /** Icon for the action button */
    open fun actionIcon(value: String): Int? = null

    /** Callback when the action button is clicked */
    open fun onClickActionButtonListener(value: String): Unit = Unit

    /** Icon for the data category */
    open val categoryIcon = ResourceUtil.drawable(parent.context, category.iconId)

    /** Message in the context menu */
    open val menuMessage =
        parent.resources.getString(R.string.dictionary_menu_message, category.name)

    /** List of items to be shown in the context menu */
    open val menuItems = properties.map {
        displayValue(it.property.value)
    }.toTypedArray()

    /** Actions for context menu items */
    open val menuActions: List<() -> Unit> by lazy {
        menuItems.map {
            val restriction = SearchRestriction.Some(category, displayValue(it))
            return@map { listener.onClickSearchButton("", restriction) }
        }
    }
}