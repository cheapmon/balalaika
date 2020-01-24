package com.github.cheapmon.balalaika.ui.widgets

import android.view.Gravity
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

class KeyValueWidget(
        private val group: ViewGroup,
        private val line: PropertyLine
) : Widget {
    override fun createView(): View {
        val widgetView = WidgetHelper.inflate(group, R.layout.lexeme_widget_key_value)
        widgetView.findViewById<TextView>(R.id.name).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            container.addView(TextView(container.context).apply {
                text = property.value
                gravity = Gravity.END
            })
        }
        return widgetView
    }

    override fun createContextMenu(): DictionaryDialog {
        val entries = line.properties.mapNotNull {
            val text = "Search dictionary for ${it.value}"
            if (it.value != null) ContextMenuEntry(text) {}
            else null
        }
        return DictionaryDialog(line.fullForm.fullForm, entries)
    }
}

object KeyValueWidgetBuilder : WidgetBuilder {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): Widget {
        return KeyValueWidget(group, line)
    }
}