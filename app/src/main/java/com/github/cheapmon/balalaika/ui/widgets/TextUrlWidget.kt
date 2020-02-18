package com.github.cheapmon.balalaika.ui.widgets

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.github.cheapmon.balalaika.ContextMenuEntry
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.home.DictionaryDialog
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope

class TextUrlWidget(
        private val group: ViewGroup,
        private val line: PropertyLine
) : Widget {
    override fun createView(): View {
        val widgetView = WidgetHelper.inflate(group, R.layout.lexeme_widget_text_url)
        widgetView.findViewById<TextView>(R.id.category).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            val value = property.value?.split(Regex(";;;"))?.first()
            val link = property.value?.split(Regex(";;;"))?.getOrNull(1)
            val view = WidgetHelper.inflate(container, R.layout.lexeme_widget_text_url_value)
            if (link != null) {
                view.findViewById<TextView>(R.id.value_with_link).text = value
                view.findViewById<ImageButton>(R.id.link_btn).setOnClickListener {
                    openLink(link)
                }
            } else {
                view.findViewById<TextView>(R.id.value).apply {
                    text = value
                    visibility = View.VISIBLE
                }
                view.findViewById<TextView>(R.id.value_with_link).visibility = View.GONE
                view.findViewById<ImageButton>(R.id.link_btn).visibility = View.GONE
            }
            container.addView(view)
        }
        return widgetView
    }

    override fun createContextMenu(): DictionaryDialog? {
        val links = line.properties.mapNotNull {
            val value = it.value?.split(Regex(";;;"))?.first()
            val link = it.value?.split(Regex(";;;"))?.getOrNull(1)
            if (link != null && value != null) Pair(value, link)
            else null
        }
        if(links.isEmpty()) return null
        val entries = links.map {
            ContextMenuEntry("Open link ${it.first}") { openLink(it.second) }
        }
        return DictionaryDialog(line.fullForm.fullForm, entries)
    }

    private fun openLink(link: String) {
        ContextCompat.startActivity(group.context, Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
    }
}

object TextUrlWidgetBuilder : WidgetBuilder {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            navController: NavController,
            group: ViewGroup,
            line: PropertyLine
    ): Widget {
        return TextUrlWidget(group, line)
    }
}