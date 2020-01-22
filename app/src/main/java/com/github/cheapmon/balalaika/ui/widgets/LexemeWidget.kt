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

class LexemeWidget(
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
            val view = WidgetHelper.inflate(group, R.layout.lexeme_widget_title)
            view.findViewById<TextView>(R.id.title).text = line.fullForm.fullForm
            if (line.fullForm.fullForm != property.value) {
                view.findViewById<TextView>(R.id.lexeme).text = property.value
            } else {
                view.findViewById<TextView>(R.id.lexeme).text = ""
            }
            container.addView(view)
        }
        return container
    }

    override fun createContextMenu(): DictionaryDialog {
        val lexemes = line.properties.mapNotNull { it.value }
        val entries = lexemes.map {
            ContextMenuEntry("Search dictionary for $it") {}
        }
        return DictionaryDialog(line.fullForm.fullForm, entries)
    }
}

object LexemeWidgetBuilder : WidgetBuilder {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): Widget {
        return LexemeWidget(group, line)
    }
}