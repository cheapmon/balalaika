package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations
import com.github.cheapmon.balalaika.databinding.HelperExampleBinding

class ExampleWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithRelations>,
    hasActions: Boolean,
    searchText: String?
) : PlainWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: PropertyWithRelations
    ): View {
        val propertyBinding: HelperExampleBinding =
            DataBindingUtil.inflate(inflater, R.layout.helper_example, parent, false)
        with(propertyBinding) {
            val parts = property.property.value.split(Regex(";;;"))
            val propValue = parts.getOrNull(1)
            title = parts.first()
            if (propValue != null) value = propValue
            else helperExampleContent.visibility = View.GONE
        }
        return propertyBinding.root
    }

    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
    override val menuActions: List<() -> Unit> = listOf()
}