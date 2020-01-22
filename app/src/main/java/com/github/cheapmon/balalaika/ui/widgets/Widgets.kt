package com.github.cheapmon.balalaika.ui.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.ui.home.DictionaryDialog
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope

interface Widget {
    fun createView(): View
    fun createContextMenu(fragmentManager: FragmentManager?): DictionaryDialog?
}

interface WidgetBuilder {
    fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): Widget
}

object WidgetHelper {
    fun inflate(group: ViewGroup, layoutId: Int): View {
        return LayoutInflater
                .from(group.context)
                .inflate(layoutId, group, false)
    }
}

object Widgets {
    private val WIDGET_CODES = mapOf(
            "plain" to PlainWidgetBuilder,
            "key_value" to KeyValueWidgetBuilder,
            "lexeme" to LexemeWidgetBuilder,
            "text_url" to TextUrlWidgetBuilder,
            "reference" to ReferenceWidgetBuilder,
            "audio" to AudioWidgetBuilder
    )

    fun get(
            fragmentManager: FragmentManager?,
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): View {
        val builder: WidgetBuilder = WIDGET_CODES.getOrElse(line.widget) { KeyValueWidgetBuilder }
        val widget = builder.create(adapter, scope, group, line)
        val view = widget.createView()
        view.setOnLongClickListener {
            widget.createContextMenu(fragmentManager)
            true
        }
        return view
    }
}

