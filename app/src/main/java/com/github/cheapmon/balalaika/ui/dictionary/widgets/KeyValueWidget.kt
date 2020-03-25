package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.databinding.HelperButtonBinding
import com.github.cheapmon.balalaika.databinding.WidgetTemplateBinding
import com.github.cheapmon.balalaika.util.IconUtil

class KeyValueWidget(
    private val parent: ViewGroup,
    private val listener: WidgetListener,
    private val category: Category,
    private val properties: List<PropertyWithRelations>
) : Widget(parent, listener, category, properties) {
    private lateinit var binding: WidgetTemplateBinding

    override fun createView(): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.widget_template, parent, false)
        binding.title = category.name
        binding.widgetTemplateIcon.setImageResource(categoryIcon)
        val inflater = LayoutInflater.from(binding.widgetTemplateContent.context)
        properties.forEach { property ->
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
                helperButton.setOnClickListener(onClickActionButtonListener)
            }
            binding.widgetTemplateContent.addView(propertyBinding.root)
        }
        return binding.root
    }

    override fun createContextMenu(): AlertDialog? = null

    fun displayValue(value: String) = value
    fun actionIcon(value: String): Int? = null

    val categoryIcon = IconUtil.getIdentifier(parent.context, category.iconId)
    val onClickActionButtonListener: View.OnClickListener? = null
}