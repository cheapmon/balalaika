package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.databinding.HelperButtonBinding
import com.github.cheapmon.balalaika.databinding.WidgetTemplateBinding
import com.github.cheapmon.balalaika.util.ResourceUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseWidget(
    val parent: ViewGroup,
    val listener: WidgetListener,
    val category: Category,
    val properties: List<PropertyWithRelations>
) : Widget(parent, listener, category, properties) {
    private lateinit var binding: WidgetTemplateBinding

    override fun createView(): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.widget_template, parent, false)
        binding.title = category.name
        binding.widgetTemplateIcon.setImageResource(categoryIcon)
        val inflater = LayoutInflater.from(binding.widgetTemplateContent.context)
        properties.forEach { property ->
            val propertyView = createPropertyView(inflater, property)
            binding.widgetTemplateContent.addView(propertyView)
        }
        return binding.root
    }

    override fun createContextMenu(): AlertDialog? = MaterialAlertDialogBuilder(parent.context)
        .setIcon(categoryIcon)
        .setTitle(menuMessage)
        .setNegativeButton(R.string.dictionary_item_cancel, null)
        .setItems(menuItems) { _, which -> menuActions[which]() }
        .show()

    open fun createPropertyView(inflater: LayoutInflater, property: PropertyWithRelations): View {
        val propertyBinding: HelperButtonBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.helper_button,
            binding.widgetTemplateContent,
            false
        )
        propertyBinding.apply {
            value = displayValue(property.property.value)
            val icon = actionIcon(property.property.value)
            if (icon != null) helperButton.setImageResource(icon)
            else helperButton.visibility = View.GONE
            helperButton.setOnClickListener { onClickActionButtonListener(property.property.value) }
        }
        return propertyBinding.root
    }

    open fun displayValue(value: String) = value
    open fun actionIcon(value: String): Int? = null
    open fun onClickActionButtonListener(value: String): Unit = Unit

    open val categoryIcon = ResourceUtil.drawable(parent.context, category.iconId)
    open val menuMessage =
        parent.resources.getString(R.string.dictionary_menu_message, category.name)
    open val menuItems = properties.map {
        displayValue(it.property.value)
    }.toTypedArray()
    open val menuActions: List<() -> Unit> = properties.map {
        val restriction = SearchRestriction.Some(category, displayValue(it.property.value))
        return@map { listener.onClickSearchButton("", restriction) }
    }
}