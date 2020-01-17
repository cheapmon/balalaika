package com.github.cheapmon.balalaika.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.ui.home.DictionaryEntry
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import com.github.cheapmon.balalaika.ui.home.PropertyLine
import kotlinx.coroutines.CoroutineScope


abstract class Widget {
    abstract fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): View
    fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    companion object {
        private val WIDGET_CODES = mapOf(
                "plain" to PlainWidget,
                "key_value" to KeyValueWidget,
                "lexeme" to LexemeWidget,
                "text_url" to TextUrlWidget,
                "reference" to ReferenceWidget
        )

        fun get(
                adapter: HomeFragment.HomeAdapter,
                scope: CoroutineScope,
                group: ViewGroup,
                line: PropertyLine
        ): View {
            val widgetClass: Widget = WIDGET_CODES.getOrElse(line.widget) { KeyValueWidget }
            return widgetClass.create(adapter, scope, group, line)
        }
    }
}

