package com.github.cheapmon.balalaika.ui.widgets

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.github.cheapmon.balalaika.ContextMenuEntry
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.home.DictionaryDialog
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope

class PlainWidget(
        private val group: ViewGroup,
        private val line: PropertyLine
) : Widget {
    override fun createView(): View {
        val container = LinearLayoutCompat(group.context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
        }
        line.properties.forEach { property ->
            val view = WidgetHelper.inflate(group, R.layout.lexeme_widget_plain).apply {
                this.findViewById<TextView>(R.id.value).text = property.value
            }
            container.addView(view)
        }
        return container
    }

    override fun createContextMenu(): DictionaryDialog {
        val values = line.properties.mapNotNull { it.value }
        val entries = values.map { ContextMenuEntry("Search dictionary for $it") {} }
        return DictionaryDialog(line.fullForm.fullForm, entries)
    }
}

object PlainWidgetBuilder : WidgetBuilder {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): Widget {
        return PlainWidget(group, line)
    }
}