package com.github.cheapmon.balalaika.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.github.cheapmon.balalaika.ContextMenuEntry
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.ui.home.DictionaryDialog
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope

abstract class Widget {
    abstract fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): View
    abstract val menuEntries: List<ContextMenuEntry>

    protected fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    private fun createContextMenu(fragmentManager: FragmentManager?, group: ViewGroup, line: PropertyLine) {
        if (menuEntries.isEmpty()) return
        val dialog = DictionaryDialog(line.fullForm.fullForm, menuEntries)
        if (fragmentManager != null) dialog.show(fragmentManager, line.widget)
    }

    companion object {
        private val WIDGET_CODES = mapOf(
                "plain" to PlainWidget,
                "key_value" to KeyValueWidget,
                "lexeme" to LexemeWidget,
                "text_url" to TextUrlWidget,
                "reference" to ReferenceWidget,
                "audio" to AudioWidget
        )

        fun get(
                fragmentManager: FragmentManager?,
                adapter: HomeFragment.HomeAdapter,
                scope: CoroutineScope,
                group: ViewGroup,
                line: PropertyLine
        ): View {
            val widgetClass: Widget = WIDGET_CODES.getOrElse(line.widget) { KeyValueWidget }
            val view = widgetClass.create(adapter, scope, group, line)
            view.setOnLongClickListener {
                widgetClass.createContextMenu(fragmentManager, group, line)
                true
            }
            return view
        }
    }
}

