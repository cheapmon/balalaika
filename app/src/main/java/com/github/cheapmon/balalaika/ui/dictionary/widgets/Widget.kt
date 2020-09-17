package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.HelperButtonBinding
import com.github.cheapmon.balalaika.databinding.WidgetTemplateBinding
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.util.ResourceUtil
import com.github.cheapmon.balalaika.util.setIconById
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// TODO: Highlight search text
abstract class Widget<T : Property>(
    protected val parent: ViewGroup,
    protected val category: DataCategory,
    protected val properties: List<T>,
    private val hasActions: Boolean,
    protected val menuListener: WidgetMenuListener,
    private val actionListener: WidgetActionListener<T>? = null
) {
    data class Views(val root: View, val content: ViewGroup)

    private fun createView(): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val (root, contentView) = createContentView(layoutInflater, category)
        properties.forEach { property ->
            val propertyView = createPropertyView(
                layoutInflater,
                contentView,
                property,
                actionListener
            )
            contentView.addView(propertyView)
        }
        return root
    }

    open fun createContextMenu(): AlertDialog? =
        MaterialAlertDialogBuilder(parent.context)
            .setIcon(categoryIcon)
            .setTitle(menuMessage)
            .setNegativeButton(R.string.cancel, null)
            .setItems(menuItems) { _, which -> menuListener.onClickMenuItem(menuItems[which]) }
            .show()

    fun create() = createView().apply {
        if (hasActions) setOnLongClickListener {
            createContextMenu()?.show()
            true
        }
    }

    open fun createContentView(
        inflater: LayoutInflater,
        category: DataCategory
    ): Views {
        val binding = WidgetTemplateBinding.inflate(inflater).apply {
            title = category.name
            widgetTemplateIcon.setImageResource(categoryIcon)
        }
        return Views(binding.root, binding.widgetTemplateContent)
    }

    open fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: T,
        actionListener: WidgetActionListener<T>?
    ): View =
        HelperButtonBinding.inflate(inflater).apply {
            helperText.text = displayName(property)
            if (hasActions) {
                actionIcon(property)?.let { helperButton.setIconById(it) }
            } else {
                helperButton.visibility = View.GONE
            }
            helperButton.setOnClickListener { actionListener?.onAction(property) }
        }.root

    abstract fun displayName(property: T): String

    @DrawableRes
    open fun actionIcon(property: T): Int? = null

    @DrawableRes
    open val categoryIcon: Int =
        ResourceUtil.drawable(parent.context, category.iconName)

    open val menuMessage: String =
        parent.resources.getString(R.string.dictionary_menu_message)
    open val menuItems =
        properties.map { displayName(it) }.toTypedArray()
}
