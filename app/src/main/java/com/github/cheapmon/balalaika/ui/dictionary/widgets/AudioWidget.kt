package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.PropertyWithRelations
import com.github.cheapmon.balalaika.util.ResourceUtil

class AudioWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithRelations>
) : BaseWidget(parent, listener, category, properties) {
    override fun displayValue(value: String): String {
        return value.split(Regex(";;;")).firstOrNull() ?: ""
    }

    override fun actionIcon(value: String): Int? {
        value.split(Regex(";;;")).getOrNull(1) ?: return null
        return R.drawable.ic_audio
    }

    override fun onClickActionButtonListener(value: String) {
        val res = value.split(Regex(";;;")).getOrNull(1) ?: return
        listener.onClickAudioButton(ResourceUtil.raw(parent.context, res))
    }

    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
    override val menuActions: List<() -> Unit> = listOf()
}